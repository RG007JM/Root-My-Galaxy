package dev.busung.s25uroot

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TargetProfileTest {
    private val profile = TargetProfile(
        profileId = "galaxy-s25-series-kernel-6.6.98",
        displayName = "Galaxy S25 series",
        models = setOf("SM-S931B", "SM-S938N"),
        kernelVersions = setOf("6.6.98"),
        exploit = RemoteArtifact("https://example.invalid/exploit", 1),
        kernelSu = RemoteArtifact("https://example.invalid/ksud", 1),
    )

    @Test
    fun matchesRegionalS25OnSameKernelVersion() {
        assertTrue(profile.matches(snapshot("SM-S931B", "6.6.98-android15-8-build-a")))
        assertTrue(profile.matches(snapshot("SM-S938N", "6.6.98-android15-8-build-b")))
    }

    @Test
    fun shizukuRequirementDefaultsOffAndCanBeEnabled() {
        assertFalse(profile.requiresShizuku)
        assertTrue(profile.copy(requiresShizuku = true).requiresShizuku)
    }


    @Test
    fun firmwareSpecificProfileRequiresExactBuildDisplay() {
        val target = TargetProfile(
            profileId = "r9q-G990BXXSLIZH2",
            displayName = "Galaxy S21 FE 5G",
            models = setOf("SM-G990B"),
            kernelVersions = setOf("5.4.289"),
            exploit = RemoteArtifact("https://example.invalid/exploit", 1),
            kernelSu = RemoteArtifact("https://example.invalid/ksud", 1),
            buildDisplays = setOf("BP2A.250605.031.A3.G990BXXSLIZH2"),
        )
        assertTrue(
            target.matches(
                snapshot(
                    "SM-G990B",
                    "5.4.289-qgki-32192773-abG990BXXSLIZH2",
                    "BP2A.250605.031.A3.G990BXXSLIZH2",
                ),
            ),
        )
        assertFalse(
            target.matches(
                snapshot(
                    "SM-G990B",
                    "5.4.289-qgki-32192773-abother-build",
                    "BP4A.250605.031.OTHER",
                ),
            ),
        )
    }

    @Test
    fun rejectsUnlistedModelOrKernelVersion() {
        assertFalse(profile.matches(snapshot("SM-S928B", "6.6.98-android15-8-build")))
        assertFalse(profile.matches(snapshot("SM-S938N", "6.6.102-android15-8-build")))
    }

    private fun snapshot(
        model: String,
        kernelRelease: String,
        buildId: String = "BP4A.251205.006.S938BCZG1",
    ) = DeviceSnapshot(
        manufacturer = "samsung",
        model = model,
        device = "unused",
        kernelRelease = kernelRelease,
        kernelVersionInfo = "#1 SMP PREEMPT",
        machine = "aarch64",
        buildId = buildId,
        fingerprint = "samsung/example",
        androidRelease = "16",
        sdk = 36,
        abi = "arm64-v8a",
        pageSize = 4096,
    )
}
