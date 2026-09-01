with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'r') as f:
    content = f.read()

# Fix the missing spaces and newlines
content = content.replace("screensimport", "screens\nimport")
content = content.replace("Colorimport", "Color\nimport")
content = content.replace("Intentimport", "Intent\nimport")
content = content.replace("backgroundimport", "background\nimport")
content = content.replace("clickableimport", "clickable\nimport")
content = content.replace("Arrangementimport", "Arrangement\nimport")
content = content.replace("Boximport", "Box\nimport")
content = content.replace("Columnimport", "Column\nimport")
content = content.replace("Rowimport", "Row\nimport")
content = content.replace("Spacerimport", "Spacer\nimport")
content = content.replace("fillMaxHeightimport", "fillMaxHeight\nimport")
content = content.replace("fillMaxSizeimport", "fillMaxSize\nimport")
content = content.replace("fillMaxWidthimport", "fillMaxWidth\nimport")

with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'w') as f:
    f.write(content)
