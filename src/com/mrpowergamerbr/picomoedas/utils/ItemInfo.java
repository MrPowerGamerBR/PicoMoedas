package com.mrpowergamerbr.picomoedas.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemInfo implements Serializable {
    private static final long serialVersionUID = 6271318490132736836L;
    
    boolean selling = false;
    boolean sellingItem = false;
    boolean closesGui = false;
    SimpleItemStack toGive;
    double price;
    String itemName;
    String openGUI;
    List<String> consoleCommands = new ArrayList<String>();
}
