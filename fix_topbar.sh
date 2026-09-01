#!/bin/bash
sed -i '/colors = TopAppBarDefaults.topAppBarColors(/i \
                actions = {\
                    company?.let { comp ->\
                        val isPro = comp.subscriptionPlan in listOf("monthly", "yearly", "three_years")\
                        val statusText = if (isPro) "Pro" else "تجريبي 14 يوم"\
                        val bgColor = if (isPro) androidx.compose.ui.graphics.Color(0xFFFFD700) else MaterialTheme.colorScheme.errorContainer\
                        val textColor = if (isPro) androidx.compose.ui.graphics.Color.Black else MaterialTheme.colorScheme.onErrorContainer\
                        Box(\
                            modifier = Modifier\
                                .padding(end = 16.dp)\
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))\
                                .background(bgColor)\
                                .padding(horizontal = 10.dp, vertical = 4.dp)\
                        ) {\
                            Text(\
                                text = statusText,\
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),\
                                color = textColor\
                            )\
                        }\
                    }\
                },\
' app/src/main/java/com/example/ui/MainScreen.kt
