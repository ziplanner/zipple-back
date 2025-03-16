package com.zipple.module.member.common.entity.category;

import lombok.Getter;

@Getter
public enum AgentSpecialty {
    //주거용 부동산
    APARTMENT("아파트"),
    HOUSE("주택/다가구"),
    VILLA("빌라/다세대"),
    ONE_ROOM_TWO_ROOM("원룸/투룸"),
    OFFICETEL("오피스텔"),

    //상업/업무용 부동산
    COMMERCIAL_SHOP("상가점포"),
    BUILDING("빌딩/상업시설"),
    OFFICE("사무실"),
    FACTORY_WAREHOUSE("공장/창고/지식산업센터"),

    //개발 및 투자 부동산
    RECONSTRUCTION("재건축/재개발"),
    LAND("토지"),
    HOSPITAL("병원/요양원"),
    AUCTION_SALE("경매/분양"),

    //특수 목적 부동산
    RELIGIOUS_FACILITY("종교시설"),
    HOTEL("호텔/모텔/펜션"),
    OTHER("기타");

    private final String description;

    AgentSpecialty(String description) {
        this.description = description;
    }

    public static AgentSpecialty getByDescription(String value) {
        if(value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }

        String normalizedDescription = value.trim();
        for(AgentSpecialty specialty : AgentSpecialty.values()) {
            if(specialty.getDescription().equals(normalizedDescription)) {
                return specialty;
            }
        }
        throw new IllegalArgumentException();
    }

    public static String getDescriptionByAgentSpecialty(AgentSpecialty agentSpecialty) {
        if(agentSpecialty == null) {
            return null;
        }
        return agentSpecialty.getDescription();
    }
}
