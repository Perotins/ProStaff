package me.perotin.prostaff.api;

import me.perotin.prostaff.ProStaff;
import me.perotin.prostaff.objects.StaffRank;

import java.util.UUID;
import java.util.stream.Collectors;

/* Created by Perotin on 9/6/18 */
public final class ProStaffAPI {

    private ProStaffAPI(){

    }

    public static void addToRank(UUID player, StaffRank rank){
        rank.addUuid(player);
    }

    public static void removeFromRank(UUID player, StaffRank rank){
        rank.getUuids().remove(player);
    }

    public static StaffRank getRank(String name){
        return ProStaff.getInstance().getRanks().stream().filter(rank -> rank.getName().equalsIgnoreCase(name)).collect(Collectors.toList()).get(0);
    }

    public static StaffRank getRank(UUID player){

        for(StaffRank rank : ProStaff.getInstance().getRanks()){
            for(UUID uuid : rank.getUuids()){
                if(uuid.equals(player)){
                    return rank;
                }
            }
        }
        return null;
    }



}
