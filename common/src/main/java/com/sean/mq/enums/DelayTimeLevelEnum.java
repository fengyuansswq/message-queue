package com.sean.mq.enums;

public enum DelayTimeLevelEnum {
    LEVEL_ZERO(0, 0L, "0s"),
    LEVEL_ONE(1, 10000L, "10s"),
    LEVEL_TWO(2, 30000L, "30s"),
    LEVEL_THREE(3, 60000L, "1m"),
    LEVEL_FOUR(4, 120000L, "2m"),
    LEVEL_FIVE(5, 180000L, "3m"),
    LEVEL_SIX(6, 240000L, "4m"),
    LEVEL_SEVEN(7, 300000L, "5m"),
    LEVEL_EIGHT(8, 360000L, "6m"),
    LEVEL_NINE(9, 420000L, "7m"),
    LEVEL_TEN(10, 480000L, "8m"),
    LEVEL_ELEVEN(11, 540000L, "9m"),
    LEVEL_TWELVE(12, 600000L, "10m"),
    LEVEL_THIRTEEN(13, 1800000L, "30m"),
    LEVEL_FOURTEEN(14, 3600000L, "1h"),
    LEVEL_FIFTEEN(15, 7200000L, "2h"),
    LEVEL_SIXTEEN(16, 21600000L, "6h"),
    LEVEL_SEVENTEEN(17, 43200000L, "12h"),
    LEVEL_EIGHTEEN(18, 86400000L, "24h");

    private int level;
    private long timeDelay;
    private String desc;

    private DelayTimeLevelEnum(int level, long timeDelay, String param5) {
        this.level = level;
        this.timeDelay = timeDelay;
        this.desc = desc;
    }

    public static DelayTimeLevelEnum getEnumByLevel(int level) {
        DelayTimeLevelEnum[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            DelayTimeLevelEnum levelEnum = var1[var3];
            if (level == levelEnum.getLevel()) {
                return levelEnum;
            }
        }

        return LEVEL_ZERO;
    }

    public int getLevel() {
        return this.level;
    }

    public long getTimeDelay() {
        return this.timeDelay;
    }

    public String getDesc() {
        return this.desc;
    }
}
