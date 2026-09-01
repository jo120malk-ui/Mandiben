#!/bin/bash
# For MainScreen.kt
sed -i '/package com.example.ui/d' app/src/main/java/com/example/ui/MainScreen.kt
sed -i '1s/^/package com.example.ui\n/' app/src/main/java/com/example/ui/MainScreen.kt

# For SubscriptionsScreen.kt
sed -i '/package com.example.ui.screens/d' app/src/main/java/com/example/ui/screens/SubscriptionsScreen.kt
sed -i '1s/^/package com.example.ui.screens\n/' app/src/main/java/com/example/ui/screens/SubscriptionsScreen.kt

