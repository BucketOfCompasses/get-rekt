/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import net.fabricmc.loom.configuration.ide.RunConfigSettings
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("fabric-loom") version "1.2-SNAPSHOT"
	id("org.quiltmc.gradle.licenser") version "2.+"
	id("io.github.juuxel.loom-quiltflower") version "1.8.0"

	kotlin("jvm") version "1.8.21"
	kotlin("plugin.serialization") version "1.5.0"
}

//loom {
//	runs {
//		named("client") {
//			vmArg("-Dmixin.debug=true")
//			vmArg("-Dmixin.debug.export=true")
//		}
//	}
//}

val javaVersion = 17

val archives_base_name: String by project
base.archivesName.set(archives_base_name)

val fabric_api_version: String by project
val fabric_kotlin_api_version: String by project
val homoglyph_version: String by project
val loader_version: String by project
val minecraft_version: String by project
val modmenu_version: String by project
val parchment_version: String by project

repositories {
	exclusiveContent {
		forRepository {
			maven {
				name = "Modrinth"
				url = uri("https://api.modrinth.com/maven")
			}
		}
		filter {
			includeGroup("maven.modrinth")
		}
	}

	exclusiveContent {
		forRepository {
			maven{
				name = "ParchmentMC Maven"
				url = uri("https://maven.parchmentmc.org")
			}
		}
		filter {
			includeGroup("org.parchmentmc.data")
		}
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${minecraft_version}")

	mappings(loom.layered {
		officialMojangMappings()
		parchment("org.parchmentmc.data:parchment-${parchment_version}@zip");
	})

	modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")
	modImplementation("net.fabricmc:fabric-language-kotlin:$fabric_kotlin_api_version")
	modImplementation("net.fabricmc:fabric-loader:$loader_version")

	modRuntimeOnly("maven.modrinth:modmenu:$modmenu_version")

	implementation("net.codebox:homoglyph:$homoglyph_version")
	include("net.codebox:homoglyph:$homoglyph_version")
}

tasks {
	processResources {
		filteringCharset = "UTF-8"

		inputs.property("version", project.version)

		filesMatching("fabric.mod.json") {
			expand(
				mapOf(
					"version" to project.version
				)
			)
		}
	}

	jar {
		from("LICENSE") {
			rename { "${it}_${base.archivesName.get()}" }
		}

		from("README.MD") {
			rename { "${it}_${base.archivesName.get()}" }
		}
	}

	withType<KotlinCompile> {
		kotlinOptions {
			jvmTarget = javaVersion.toString()

			// languageVersion: A.B of the kotlin plugin version A.B.C
			languageVersion = "1.8"
		}
	}

	val targetJavaVersion = JavaVersion.toVersion(javaVersion)
	if (JavaVersion.current() < targetJavaVersion) {
		kotlin.jvmToolchain(javaVersion)

		java.toolchain {
			languageVersion.set(JavaLanguageVersion.of(javaVersion))
		}
	}

	withType<JavaCompile> {
		options.encoding = "UTF-8"
		options.isDeprecation = true
		options.release.set(javaVersion)
	}

	java {
		withSourcesJar()

		sourceCompatibility = targetJavaVersion
		targetCompatibility = targetJavaVersion
	}

	license {
		rule(file("LICENSE_HEADER"))
	}
}
