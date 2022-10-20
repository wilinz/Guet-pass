package com.wilinz.guet_pass.tools

import com.wilinz.guet_pass.ui.activity.Item
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

fun generateItem(): List<Item> {
    val list = mutableListOf<Item>()
    val today = Instant.now().truncatedTo(ChronoUnit.DAYS)
    list.add(
        Item(
            "花江后街",
            listOf("出场", "用户扫描", "用户填报", "访客"),
            time = format2.format(Instant.now())
        )
    )
    for (i in 0 until 10) {
        val time = today.plusSeconds((-3600L * 24L * (i + 1)))
        var timeRange1: LongRange? = null
        if ((0..1).random() == 1) {
            val time1 = time.plusSeconds(getRandomTime(endHour = 22-4))
            val time2 = time1.plusSeconds((60..240L).random() * 60)
            timeRange1 =
                (time1.toEpochMilli() / 1000 - 45 * 60..time2.toEpochMilli() / 1000 + 45 * 60)
            list.add(
                Item(
                    "花江后街",
                    listOf("入场", "用户扫描", "用户填报", "访客"),
                    time = format
                        .format(time2)
                )
            )
            list.add(
                Item(
                    "花江后街",
                    listOf("出场", "用户扫描", "用户填报", "访客"),
                    time = format
                        .format(time1)
                )
            )
        }
        for (i in 0 until (1..2).random()) {
            var time2: Instant
            while (true) {
                time2 = time.plusSeconds(getRandomTime())
                if (timeRange1 != null) {
                    if (time2.toEpochMilli() / 1000 !in timeRange1) {
                        break
                    }
                } else {
                    break
                }
            }
            list.add(
                Item(
                    "男生D2区检查点",
                    listOf("入场", "用户扫描", "用户填报"),
                    time = format
                        .format(time2)
                )
            )
        }
    }
    list.sortByDescending { it.time }
    return list
}

fun getRandomTime(startHour:Int=9,endHour:Int=20) = (startHour..endHour).random() * 3600L + (0..59).random() * 60
val format = DateTimeFormatter.ofPattern("MM月dd日 HH:mm").withLocale(Locale.CHINA)
    .withZone(ZoneId.of("UTC"))

val format2 = DateTimeFormatter.ofPattern("MM月dd日 HH:mm").withLocale(Locale.CHINA)
    .withZone(ZoneId.of("UTC+8"))