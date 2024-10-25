/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import entities.RoomTypeEntity;
import java.util.Comparator;

/**
 *
 * @author mattl
 */
public class RankingComparator implements Comparator<RoomTypeEntity> {

    public RankingComparator() {
    }
    
    public int compare(RoomTypeEntity roomType1, RoomTypeEntity roomType2) {
        return roomType1.getRanking() - roomType2.getRanking();
    }
}
