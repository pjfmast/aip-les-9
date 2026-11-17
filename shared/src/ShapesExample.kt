package com.example.aip_les_9

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.aip_les_9.resources.Res
import com.example.aip_les_9.resources.profile_picture
import org.jetbrains.compose.resources.painterResource

@Composable
fun ShapesExample(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Shapes & Modifier Volgorde Demonstratie",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Overzicht van hoe modifier chains in Jetpack Compose werken en hoe de volgorde van clip, border, padding en background direct invloed heeft op het visuele resultaat.",
            style = MaterialTheme.typography.bodyMedium
        )

        // Card 1: Basis clip op vierkante afbeelding
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "1. Origineel (Vierkant) vs Geclipt (Rond / Schuine hoeken)",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Bij de originele afbeelding vult de foto het hele vierkant. Zodra we 'clip()' toepassen, worden de hoeken afgekapt:",
                    style = MaterialTheme.typography.bodySmall
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top
                ) {
                    // Origineel vierkant
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = "Origineel (Vierkant)",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Alle hoeken zichtbaar",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = painterResource(Res.drawable.profile_picture),
                            contentDescription = "Originele vierkante afbeelding",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RectangleShape)
                        )
                    }

                    // CircleShape
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = "clip(CircleShape)",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Hoeken vallen weg",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = painterResource(Res.drawable.profile_picture),
                            contentDescription = "Geclipt met CircleShape",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                        )
                    }

                    // CutCornerShape
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = "clip(CutCorner)",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Schuine hoeken",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = painterResource(Res.drawable.profile_picture),
                            contentDescription = "Geclipt met CutCornerShape",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CutCornerShape(20.dp))
                        )
                    }
                }
            }
        }

        // Card 2: Waarom clip & size alleen geen verschil tonen
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "2. Waarom 'clip -> size' en 'size -> clip' gelijk tonen",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Zonder tussenliggende modifiers bepalen 'clip' (tekenlaag) en 'size' (layout-afmeting) hetzelfde afgeknotte 100x100 dp cirkelgebied:",
                    style = MaterialTheme.typography.bodySmall
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "clip(Circle) -> size(100.dp)",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Voorbeeld uit het document",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = painterResource(Res.drawable.profile_picture),
                            contentDescription = "Profile Picture (clip then size)",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(100.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "size(100.dp) -> clip(Circle)",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Alternatieve volgorde",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = painterResource(Res.drawable.profile_picture),
                            contentDescription = "Profile Picture (size then clip)",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                        )
                    }
                }
            }
        }

        // Card 3: Zichtbaar effect met Border en Clip volgorde
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "3. Zichtbaar effect met Rand (Border) & Clip volgorde",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Plaatsing van 'border' vóór of ná 'clip' verandert de vorm en positie van de rand:",
                    style = MaterialTheme.typography.bodySmall
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top
                ) {
                    // Border vóór clip: vierkante border rondom ronde foto
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = "border -> clip",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Vierkante rand om cirkel",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = painterResource(Res.drawable.profile_picture),
                            contentDescription = "Border before clip",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .border(3.dp, MaterialTheme.colorScheme.primary)
                                .clip(CircleShape)
                        )
                    }

                    // Clip vóór border (met CircleShape): ronde rand volgt foto exact
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = "clip -> border(Circle)",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ronde rand volgt cirkel",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = painterResource(Res.drawable.profile_picture),
                            contentDescription = "Clip before border",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        )
                    }
                }
            }
        }

        // Card 4: Zichtbaar effect met Padding en Clip volgorde
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "4. Zichtbaar effect met Padding & Clip volgorde",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Padding vóór clip gedraagt zich als buitenmarge (margin) waardoor de geclipte cirkel kleiner wordt. Padding ná clip werkt als binnenmarge (binnen de cirkelvorm):",
                    style = MaterialTheme.typography.bodySmall
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top
                ) {
                    // Padding VOOR Clip
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = "padding(14.dp) -> clip",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Kleinere cirkel (margin)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = painterResource(Res.drawable.profile_picture),
                            contentDescription = "Padding before clip",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(14.dp)
                                .clip(CircleShape)
                        )
                    }

                    // Clip VOOR Padding
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = "clip -> padding(14.dp)",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Grote cirkel met binnenrand",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = painterResource(Res.drawable.profile_picture),
                            contentDescription = "Clip before padding",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(14.dp)
                        )
                    }
                }
            }
        }

        // Card 5: Combinatie van Border + Padding + Clip (Profiel Avatar patroon)
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "5. Combinatie: Border + Padding + Clip + Background",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Praktisch voorbeeld: Een avatar met buitenrand, tussenruimte en een ronde foto door modifiers in de juiste volgorde te stapelen:",
                    style = MaterialTheme.typography.bodySmall
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top
                ) {
                    // Volgorde A: Cirkelrand -> Tussenruimte -> Ronde foto
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = "Ronde Avatar met Spacing",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "border -> pad -> clip",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = painterResource(Res.drawable.profile_picture),
                            contentDescription = "Ronde avatar met rand en tussenruimte",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                .padding(6.dp)
                                .clip(CircleShape)
                        )
                    }

                    // Volgorde B: Vierkante rand -> Tussenruimte -> Ronde foto
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = "Vierkante Rand met Spacing",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "border(sq) -> pad -> clip",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = painterResource(Res.drawable.profile_picture),
                            contentDescription = "Vierkante rand met ronde foto",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .border(3.dp, MaterialTheme.colorScheme.tertiary)
                                .padding(6.dp)
                                .clip(CircleShape)
                        )
                    }

                    // Volgorde C: Gekleurde badge ring
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = "Badge Ring Achtergrond",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "clip -> bg -> pad -> clip",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = painterResource(Res.drawable.profile_picture),
                            contentDescription = "Gekleurde badge ring",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(6.dp)
                                .clip(CircleShape)
                        )
                    }
                }
            }
        }

        // Card 6: Uitleg en best practices
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "6. Vuistregels voor Modifier Volgorde",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "• Buitenmarge vs Binnenmarge: 'padding' vóór 'clip/background' creëert ruimte aan de buitenkant (margin). 'padding' ná 'clip/background' creëert ruimte aan de binnenkant.\n" +
                            "• Vorm van de Rand: Een 'border' vóór 'clip' wordt getekend op de oorspronkelijke rechthoekige vorm van de box. Om een afgeronde rand te krijgen, geef je dezelfde shape mee aan 'border(..., shape)' of plaats je 'clip' op het juiste moment.\n" +
                            "• Layering: Modifiers transformeren de tekenlaag stap voor stap van boven naar beneden.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
