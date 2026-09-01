with open('app/src/main/java/com/example/ui/MainScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("AppTab.SALES -> {", "AppTab.SALES, MoreOption.SALES -> {")
content = content.replace("AppTab.REPORTS -> {", "AppTab.REPORTS, MoreOption.REPORTS -> {")

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'w') as f:
    f.write(content)
