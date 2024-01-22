package com.bluetriangle.android.demo.tests

class MemoryStackTest(
    val interval: Long = 30L
) : BTTTestCase {
    override val title: String
        get() = "Memory Stack Test"
    override val description: String
        get() = "Allocates stack memory for  $interval secs"

    override fun run(): String? {
        allocateStackMemory(0, 20 * 1024)
        return null
    }

    private fun allocateStackMemory(counter: Int, max: Int) {
        // Allocating 128 double variables
        // each double variable equals 8 bytes
        // 8 * 128 = 1024 bytes = 1Kb
        val a0 = 0.0
        val a1 = 0.0
        val a2 = 0.0
        val a3 = 0.0
        val a4 = 0.0
        val a5 = 0.0
        val a6 = 0.0
        val a7 = 0.0
        val a8 = 0.0
        val a9 = 0.0
        val a10 = 0.0
        val a11 = 0.0
        val a12 = 0.0
        val a13 = 0.0
        val a14 = 0.0
        val a15 = 0.0
        val a16 = 0.0
        val a17 = 0.0
        val a18 = 0.0
        val a19 = 0.0
        val a20 = 0.0
        val a21 = 0.0
        val a22 = 0.0
        val a23 = 0.0
        val a24 = 0.0
        val a25 = 0.0
        val a26 = 0.0
        val a27 = 0.0
        val a28 = 0.0
        val a29 = 0.0
        val a30 = 0.0
        val a31 = 0.0
        val a32 = 0.0
        val a33 = 0.0
        val a34 = 0.0
        val a35 = 0.0
        val a36 = 0.0
        val a37 = 0.0
        val a38 = 0.0
        val a39 = 0.0
        val a40 = 0.0
        val a41 = 0.0
        val a42 = 0.0
        val a43 = 0.0
        val a44 = 0.0
        val a45 = 0.0
        val a46 = 0.0
        val a47 = 0.0
        val a48 = 0.0
        val a49 = 0.0
        val a50 = 0.0
        val a51 = 0.0
        val a52 = 0.0
        val a53 = 0.0
        val a54 = 0.0
        val a55 = 0.0
        val a56 = 0.0
        val a57 = 0.0
        val a58 = 0.0
        val a59 = 0.0
        val a60 = 0.0
        val a61 = 0.0
        val a62 = 0.0
        val a63 = 0.0
        val a64 = 0.0
        val a65 = 0.0
        val a66 = 0.0
        val a67 = 0.0
        val a68 = 0.0
        val a69 = 0.0
        val a70 = 0.0
        val a71 = 0.0
        val a72 = 0.0
        val a73 = 0.0
        val a74 = 0.0
        val a75 = 0.0
        val a76 = 0.0
        val a77 = 0.0
        val a78 = 0.0
        val a79 = 0.0
        val a80 = 0.0
        val a81 = 0.0
        val a82 = 0.0
        val a83 = 0.0
        val a84 = 0.0
        val a85 = 0.0
        val a86 = 0.0
        val a87 = 0.0
        val a88 = 0.0
        val a89 = 0.0
        val a90 = 0.0
        val a91 = 0.0
        val a92 = 0.0
        val a93 = 0.0
        val a94 = 0.0
        val a95 = 0.0
        val a96 = 0.0
        val a97 = 0.0
        val a98 = 0.0
        val a99 = 0.0
        val a100 = 0.0
        val a101 = 0.0
        val a102 = 0.0
        val a103 = 0.0
        val a104 = 0.0
        val a105 = 0.0
        val a106 = 0.0
        val a107 = 0.0
        val a108 = 0.0
        val a109 = 0.0
        val a110 = 0.0
        val a111 = 0.0
        val a112 = 0.0
        val a113 = 0.0
        val a114 = 0.0
        val a115 = 0.0
        val a116 = 0.0
        val a117 = 0.0
        val a118 = 0.0
        val a119 = 0.0
        val a120 = 0.0
        val a121 = 0.0
        val a122 = 0.0
        val a123 = 0.0
        val a124 = 0.0
        val a125 = 0.0
        val a126 = 0.0
        val a127 = 0.0
        val a128 = 0.0

        val a =
            a0 + a1 + a2 + a3 + a4 + a5 + a6 + a7 + a8 + a9 + a10 + a11 + a12 + a13 + a14 + a15 + a16 + a17 + a18 + a19 + a20 + a21 + a22 + a23 + a24 + a25 + a26 + a27 + a28 + a29 + a30 + a31 + a32 + a33 + a34 + a35 + a36 + a37 + a38 + a39 + a40 + a41 + a42 + a43 + a44 + a45 + a46 + a47 + a48 + a49 + a50 + a51 + a52 + a53 + a54 + a55 + a56 + a57 + a58 + a59 + a60 + a61 + a62 + a63 + a64 + a65 + a66 + a67 + a68 + a69 + a70 + a71 + a72 + a73 + a74 + a75 + a76 + a77 + a78 + a79 + a80 + a81 + a82 + a83 + a84 + a85 + a86 + a87 + a88 + a89 + a90 + a91 + a92 + a93 + a94 + a95 + a96 + a97 + a98 + a99 + a100 + a101 + a102 + a103 + a104 + a105 + a106 + a107 + a108 + a109 + a110 + a111 + a112 + a113 + a114 + a115 + a116 + a117 + a118 + a119 + a120 + a121 + a122 + a123 + a124 + a125 + a126 + a127 + a128
        if (counter < max) {
            allocateStackMemory(counter + 1, max)
        } else {
            Thread.sleep(interval * 1000)
        }
        println(a)
    }
}