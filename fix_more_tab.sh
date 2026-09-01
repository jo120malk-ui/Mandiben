#!/bin/bash
sed -i '/AppTab.REPORTS -> {/i \
                    AppTab.MORE -> {\
                        LazyVerticalGrid(\
                            columns = GridCells.Fixed(2),\
                            modifier = Modifier.fillMaxSize().padding(16.dp),\
                            horizontalArrangement = Arrangement.spacedBy(16.dp),\
                            verticalArrangement = Arrangement.spacedBy(16.dp)\
                        ) {\
                            items(moreItems) { item ->\
                                Card(\
                                    modifier = Modifier.fillMaxWidth().clickable { viewModel.selectMoreOption(item.option) },\
                                    shape = RoundedCornerShape(24.dp),\
                                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)\
                                ) {\
                                    Column(\
                                        modifier = Modifier.padding(24.dp),\
                                        horizontalAlignment = Alignment.CenterHorizontally,\
                                        verticalArrangement = Arrangement.Center\
                                    ) {\
                                        Icon(\
                                            imageVector = item.icon,\
                                            contentDescription = item.label,\
                                            tint = MaterialTheme.colorScheme.primary,\
                                            modifier = Modifier.size(32.dp)\
                                        )\
                                        Spacer(modifier = Modifier.height(12.dp))\
                                        Text(\
                                            text = item.label,\
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),\
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,\
                                            textAlign = TextAlign.Center\
                                        )\
                                    }\
                                }\
                            }\
                        }\
                    }\
' app/src/main/java/com/example/ui/MainScreen.kt
