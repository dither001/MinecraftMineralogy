package com.mcmoddev.lib.util;

import java.util.HashMap;
import java.util.Map;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mcmoddev.lib.exceptions.ItemNotFoundException;
import com.mcmoddev.lib.exceptions.MaterialNotFoundException;
import com.mcmoddev.lib.exceptions.TabNotFoundException;
import com.mcmoddev.lib.interfaces.IDynamicTabProvider;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public final class DynamicTabProvider implements IDynamicTabProvider {
	private Map<String, MMDCreativeTab> tabs = new HashMap<>();
	private Map<String, String> tabsByMod = new HashMap<>();
	
	private Multimap<String, String> tabItemMapping = ArrayListMultimap.create();
		
	private MMDCreativeTab getTabByName(String tabName) throws TabNotFoundException {
		MMDCreativeTab tab = tabs.get(tabName);
		
		if (tab == null)
			throw new TabNotFoundException(tabName);
		
		return tab;
	}

	@Override
	public void addToTab(String tabName, Block block) throws TabNotFoundException {
		block.setCreativeTab(getTabByName(tabName));
	}
	
	@Override
	public void addToTab(String tabName, Item item) throws TabNotFoundException {	
		item.setCreativeTab(getTabByName(tabName));
	}

	@Override
	public void setIcon(String tabName, String materialName) throws TabNotFoundException, MaterialNotFoundException {
//		Block temp;
//		ItemStack blocksTabIconItem;
//
//		MMDMaterial material = Materials.getMaterialByName(materialName);
//		
//		if (material.getName().equals(materialName) && (material.hasBlock(Names.BLOCK)))
//			temp = material.getBlock(Names.BLOCK);
//		else
//			temp = net.minecraft.init.Blocks.IRON_BLOCK;
//		
//		blocksTabIconItem = new ItemStack(Item.getItemFromBlock(temp));
//		blocksTab.setTabIconItem(blocksTabIconItem);
	}

	private String getTab(String itemName, String modID)  {
		for (String tab : tabItemMapping.get(itemName))
			if (modID.equals(tabsByMod.get(tab))) 
				return tab;
		
		return "";
	}

	public void setTabItemMapping(String tabName, String itemName) {
		tabItemMapping.put(itemName, tabName);
	}

	private String getTab(String itemName)   {
		return tabItemMapping.get(itemName).stream().findFirst().orElse("");
	}

	@Override
	public void addTab(String tabName, boolean searchable, String modID) {
		String internalTabName = String.format("%s.%s", modID, tabName);
		MMDCreativeTab tab = new MMDCreativeTab(internalTabName, searchable);
		
		tabs.put(tabName, tab);
		tabsByMod.put(tabName, modID);
	}

	@Override
	public void addToTab(Block block) throws TabNotFoundException, ItemNotFoundException {
		addToTab(getTab(block), block);
	}

	@Override
	public void addToTab(Item item) throws TabNotFoundException, ItemNotFoundException {
		addToTab(getTab(item), item);
	}

	@Override
	public String[] getTabs() {
		return tabs.keySet().toArray(new String[0]);
	}

	@Override
	public void initialiseRetrospectiveTabGeneration() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeRetrospectiveTabGeneration() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getTab(Item item) throws ItemNotFoundException {
		return getTabBySequence(item.getRegistryName().getResourcePath(), 
				item.getRegistryName().getResourceDomain(), item.getClass().getSimpleName());
	}

	@Override
	public String getTab(Block block) throws ItemNotFoundException {	
		return getTabBySequence(block.getRegistryName().getResourcePath(), 
				block.getRegistryName().getResourceDomain(), block.getClass().getSimpleName());
	}

	private String getTabBySequence(String path, String domain, String simpleName) throws ItemNotFoundException {
		String tab;
		
		// 1) Try to get a tab mapping based on item name and mod id
		tab = getTab(path, domain);
		if (!tab.isEmpty()) return tab;
		
		//2) Try to get a tab mapping based on item name
		tab = getTab(path);
		if (!tab.isEmpty()) return tab;
		
		//3) Try to get a tab mapping based on item class name and mod id
		tab = getTab(simpleName, domain);
		if (!tab.isEmpty()) return tab;
		
		//4) Try to get a tab mapping based on item class name only
		tab = getTab(simpleName);
		if (!tab.isEmpty()) return tab;
		
		throw new ItemNotFoundException(path);
	}
}
