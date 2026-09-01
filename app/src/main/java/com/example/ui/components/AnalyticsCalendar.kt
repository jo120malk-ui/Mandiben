package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import java.util.Locale

@Composable
fun AnalyticsCalendar(
    activeDatesMs: List<Long>,
    selectedDateMs: Long?,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // We default the display month to the selected date or current date
    var currentDisplayMonth by remember(selectedDateMs) { 
        mutableStateOf(getStartOfMonth(selectedDateMs ?: System.currentTimeMillis())) 
    }
    
    val cal = Calendar.getInstance()
    cal.timeInMillis = currentDisplayMonth
    val displayMonth = cal.get(Calendar.MONTH)
    val displayYear = cal.get(Calendar.YEAR)
    
    // Arabic month name
    val monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("ar")) ?: ""
    
    // Background and accent colors
    val darkBg = Color(0xFF111111)
    val neonGreen = Color(0xFFC6FF00)
    val textWhite = Color.White
    val textGray = Color(0xFF666666)
    
    // Convert activeDates to a set of start-of-day timestamps for fast lookup
    val activeDays = remember(activeDatesMs) {
        activeDatesMs.map { getStartOfDayForReports(it) }.toSet()
    }
    val selectedDayStart = selectedDateMs?.let { getStartOfDayForReports(it) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(darkBg)
            .padding(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { 
                val c = Calendar.getInstance().apply { timeInMillis = currentDisplayMonth }
                c.add(Calendar.MONTH, -1)
                currentDisplayMonth = c.timeInMillis
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "الشهر السابق", tint = textGray)
            }
            
            Text(
                text = "$monthName $displayYear",
                color = textWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            
            IconButton(onClick = { 
                val c = Calendar.getInstance().apply { timeInMillis = currentDisplayMonth }
                c.add(Calendar.MONTH, 1)
                currentDisplayMonth = c.timeInMillis
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "الشهر التالي", tint = textGray)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Weekdays in Arabic (Starting from Sunday)
        val weekdays = listOf("أحد", "إثنين", "ثلاثاء", "أربعاء", "خميس", "جمعة", "سبت")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            weekdays.forEach { day ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(text = day, color = textGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Days Grid
        val daysInMonth = getDaysInMonth(displayYear, displayMonth)
        val firstDayOfWeek = getFirstDayOfWeek(displayYear, displayMonth) // 1 = Sunday
        val offset = firstDayOfWeek - 1
        
        val totalCells = if (offset + daysInMonth > 35) 42 else 35
        
        // Calculate previous month's days
        val prevCal = Calendar.getInstance().apply { 
            timeInMillis = currentDisplayMonth
            add(Calendar.MONTH, -1)
        }
        val daysInPrevMonth = getDaysInMonth(prevCal.get(Calendar.YEAR), prevCal.get(Calendar.MONTH))
        
        Column(modifier = Modifier.fillMaxWidth()) {
            for (row in 0 until (totalCells / 7)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    for (col in 0..6) {
                        val cellIndex = row * 7 + col
                        val dayNumber = cellIndex - offset + 1
                        
                        var isCurrentMonth = true
                        var displayNum = dayNumber
                        var dayStartMs = 0L
                        
                        if (dayNumber < 1) {
                            isCurrentMonth = false
                            displayNum = daysInPrevMonth + dayNumber
                        } else if (dayNumber > daysInMonth) {
                            isCurrentMonth = false
                            displayNum = dayNumber - daysInMonth
                        } else {
                            val c = Calendar.getInstance().apply {
                                set(displayYear, displayMonth, dayNumber, 0, 0, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            dayStartMs = c.timeInMillis
                        }
                        
                        val isSelected = isCurrentMonth && dayStartMs == selectedDayStart
                        val hasEvent = isCurrentMonth && activeDays.contains(dayStartMs)
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clickable(enabled = isCurrentMonth) {
                                    if (isCurrentMonth) {
                                        onDateSelected(dayStartMs)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // Radial Glow for events
                            if (hasEvent) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(neonGreen.copy(alpha = 0.25f), Color.Transparent),
                                            center = center,
                                            radius = size.minDimension / 1.5f
                                        )
                                    )
                                }
                            }
                            
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = displayNum.toString(),
                                    color = if (isCurrentMonth) textWhite else textGray,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                // Indicator
                                if (isSelected) {
                                    Box(modifier = Modifier.width(16.dp).height(2.dp).background(neonGreen))
                                } else if (hasEvent) {
                                    Box(modifier = Modifier.size(4.dp).clip(androidx.compose.foundation.shape.CircleShape).background(neonGreen))
                                } else {
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getStartOfDayForReports(timeMs: Long): Long {
    val cal = Calendar.getInstance()
    cal.timeInMillis = timeMs
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

private fun getStartOfMonth(timeMs: Long): Long {
    val cal = Calendar.getInstance()
    cal.timeInMillis = timeMs
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

private fun getDaysInMonth(year: Int, month: Int): Int {
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, year)
    cal.set(Calendar.MONTH, month)
    return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
}

private fun getFirstDayOfWeek(year: Int, month: Int): Int {
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, year)
    cal.set(Calendar.MONTH, month)
    cal.set(Calendar.DAY_OF_MONTH, 1)
    return cal.get(Calendar.DAY_OF_WEEK)
}
