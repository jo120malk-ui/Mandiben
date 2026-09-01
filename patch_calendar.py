with open('app/src/main/java/com/example/ui/components/AnalyticsCalendar.kt', 'r') as f:
    content = f.read()

content = content.replace("private fun getStartOfDay", "fun getStartOfDayForReports")
content = content.replace("getStartOfDay(", "getStartOfDayForReports(")

with open('app/src/main/java/com/example/ui/components/AnalyticsCalendar.kt', 'w') as f:
    f.write(content)
