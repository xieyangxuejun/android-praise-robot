package com.foretree.praiserobot

/**
 * Created by silen on 2018/9/24 0:31
 * Copyright (c) 2018 in FORETREE
 */
enum class PackageEnum(val packageName: String) {
    JD("com.jingdong.app.mall"),
    ELEME("me.ele"),
    NOW("com.tencent.now"),
    ERROR("error");

    companion object {
        fun from(packageName: String): PackageEnum {
            values().forEach {
                if (it.packageName == packageName) return it
            }
            return ERROR
        }
    }
}

enum class GiftEnum(val giftName: String) {
    Flower("小红花"),
    Ceremony("入团礼"),
    DogFood("狗粮"),
    Muah("么么哒"),
    IceCream("冰淇淋"),
    Rose("玫瑰花"),
    Donut("甜甜圈"),
    ShowHeart("爱你哦"),
    LuckyStar("幸运星"),
    WindMill("旋转风车"),
    MagicPotion("魔法药水"),
    Soap("肥皂"),
    Mask("魅影面具"),
    Town("童话小镇"),
    Whale("星空鲸鱼"),
    GreenWildElf("绿野精灵"),
    Bear("小熊"),
    Diamond("钻石"),
    SuperCar("超级跑车"),
    RomanticFireworks("浪漫烟花"),
    Cake("蛋糕"),
    VictoryGesture("胜利手势"),
    RoyalPlane("皇家飞机"),
    ElopeToSpace("私奔到太空"),
    RollerCoaster("火山车"),
    FerrisWheel("摩天轮"),
    Circus("马戏团"),
    Gift("礼物");

    companion object {
        fun from(giftName: String): GiftEnum {
            GiftEnum.values().forEach {
                if (it.giftName == giftName) return it
            }
            return GiftEnum.Gift
        }
    }

}