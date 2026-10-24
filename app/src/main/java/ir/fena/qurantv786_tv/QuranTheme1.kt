package ir.fena.qurantv786_tv

object QuranTheme1 {
    // -webkit-text-stroke: 2px #393E46;
    private val themes = listOf(
        // تم ۱: بنفش-آبی-فیروزه‌ای (نمونهٔ درخواستی شما)
        Theme(
            quranTextStyle = """
                font-size: 54px;
                color: #fff;
                text-shadow:-2px -2px 0 #000,2px -2px 0 #000,-2px  2px 0 #000,2px  2px 0 #000,-3px 0 0 #000,3px 0 0 #000,0 -3px 0 #000,0  3px 0 #000,0 0 8px rgba(0,0,0,.8);
                line-height: 1.9;
                position: relative;
                z-index: 2;
            """,
            translatorStyle = """
                font-size: 42px;
                position: relative;
                z-index: 2;
            """,
            infoStyle = """
                font-size: 42px;
                color: #fff;
                text-shadow:-2px -2px 0 #000,2px -2px 0 #000,-2px  2px 0 #000,2px  2px 0 #000,-3px 0 0 #000,3px 0 0 #000,0 -3px 0 #000,0  3px 0 #000,0 0 8px rgba(0,0,0,.8);
                margin-top: 12px;
                position: relative;
                z-index: 2;
            """,
            fontFace = """
                @font-face {
                    font-family: 'Shahrzad';
                    src: url('file:///android_asset/fonts/Shahrzad.ttf');
                }
            """,
            name = "Turquoise Purple"
        ),
        // تم ۲: طلایی-زرشکی (رنگ‌های گرم)
        Theme(
            quranTextStyle = """
                font-size: 54px;
                color: #499A13;
                text-shadow:-2px -2px 0 #000,2px -2px 0 #000,-2px 2px 0 #000,2px 2px 0 #000,-3px 0 0 #000,3px 0 0 #000,0 -3px 0 #000,0 3px 0 #000,0 0 6px #00d9ff,0 0 12px #00d9ff,0 0 20px #00d9ff,0 0 30px #00d9ff; 
                line-height: 1.9;
                position: relative;
                z-index: 2;
            """,
            translatorStyle = """
                font-size: 42px;
                color: #499A13;
                font-style: italic;
                position: relative;
                z-index: 2;
            """,
            infoStyle = """
                font-size: 42px;
                color: #499A13;
                text-shadow:-2px -2px 0 #000,2px -2px 0 #000,-2px 2px 0 #000,2px 2px 0 #000,-3px 0 0 #000,3px 0 0 #000,0 -3px 0 #000,0 3px 0 #000,0 0 6px #00d9ff,0 0 12px #00d9ff,0 0 20px #00d9ff,0 0 30px #00d9ff;
                margin-top: 12px;
                position: relative;
                z-index: 2;
            """,
            fontFace = """
                @font-face {
                    font-family: 'Shahrzad';
                    src: url('file:///android_asset/fonts/Shahrzad.ttf');
                }
            """,
            name = "Golden Crimson"
        ),
        // تم ۳: اقیانوسی (آبی‌های عمیق)
        Theme(
            quranTextStyle = """
                font-size: 54px;
                color: #65DCD5;
                text-shadow:-2px -2px 0 #000,2px -2px 0 #000,-2px 2px 0 #000,2px 2px 0 #000,-3px 0 0 #000,3px 0 0 #000,0 -3px 0 #000,0 3px 0 #000,0 0 6px #00ff66,0 0 12px #00ff66,0 0 20px #00ff66,0 0 30px #00ff66;
                line-height: 1.9;
                background: rgba(238, 238, 238, 0.5);
                position: relative;
                z-index: 2;
            """,
            translatorStyle = """
                font-size: 42px;
                color: #65DCD5;
                text-shadow: 3px 3px 8px #A2D2FF;
                margin-top: 24px;
                font-style: italic;
                position: relative;
                z-index: 2;
            """,
            infoStyle = """
                font-size: 42px;
                color: #65DCD5;
                text-shadow:-2px -2px 0 #000,2px -2px 0 #000,-2px 2px 0 #000,2px 2px 0 #000,-3px 0 0 #000,3px 0 0 #000,0 -3px 0 #000,0 3px 0 #000,0 0 6px #00ff66,0 0 12px #00ff66,0 0 20px #00ff66,0 0 30px #00ff66;
                background: rgba(238, 238, 238, 0.5);
                margin-top: 12px;
                position: relative;
                z-index: 2;
            """,
            fontFace = """
                @font-face {
                    font-family: 'Shahrzad';
                    src: url('file:///android_asset/fonts/Shahrzad.ttf');
                }
            """,
            name = "Oceanic"
        ),
        // تم ۴: جنگلی (سبزهای طبیعی)
        Theme(
            quranTextStyle = """
                font-size: 54px;
                color: #FF80C7;
                text-shadow:-2px -2px 0 #000,2px -2px 0 #000,-2px 2px 0 #000,2px 2px 0 #000,-3px 0 0 #000,3px 0 0 #000,0 -3px 0 #000,0 3px 0 #000,0 0 6px #ff00ff,0 0 12px #ff00ff,0 0 20px #ff00ff,0 0 30px #ff00ff;
                line-height: 1.9;
                position: relative;
                z-index: 2;
            """,
            translatorStyle = """
                font-size: 42px;
                color: #FF80C7;
                text-shadow: 3px 3px 8px #C9E4C5;
                margin-top: 24px;
                font-style: italic;
                position: relative;
                z-index: 2;
            """,
            infoStyle = """
                font-size: 42px;
                color: #FF80C7;
                text-shadow:-2px -2px 0 #000,2px -2px 0 #000,-2px 2px 0 #000,2px 2px 0 #000,-3px 0 0 #000,3px 0 0 #000,0 -3px 0 #000,0 3px 0 #000,0 0 6px #ff00ff,0 0 12px #ff00ff,0 0 20px #ff00ff,0 0 30px #ff00ff;
                margin-top: 12px;
                position: relative;
                z-index: 2;
            """,
            fontFace = """
                @font-face {
                    font-family: 'Shahrzad';
                    src: url('file:///android_asset/fonts/Shahrzad.ttf');
                }
            """,
            name = "Forest"
        ),
        // تم ۵: برعکس (متن روشن با سایهٔ تیره)
        Theme(
            quranTextStyle = """
                font-size: 54px;
                color: #fffef2;
                text-shadow:-2px -2px 0 #000,2px -2px 0 #000,-2px 2px 0 #000,2px 2px 0 #000,-3px 0 0 #000,3px 0 0 #000,0 -3px 0 #000,0 3px 0 #000,0 0 4px #fff,0 0 8px #ffe066,0 0 16px #ffd700,0 0 28px #ffb300,0 0 42px rgba(255,200,0,.85);
                line-height: 1.9;
                position: relative;
                z-index: 2;
            """,
            translatorStyle = """
                font-size: 42px;
                color: #fffef2;
                text-shadow: 3px 3px 8px #2A0944;
                margin-top: 24px;
                font-style: italic;
                position: relative;
                z-index: 2;
            """,
            infoStyle = """
                font-size: 42px;
                color: #fffef2;
                text-shadow:-2px -2px 0 #000,2px -2px 0 #000,-2px 2px 0 #000,2px 2px 0 #000,-3px 0 0 #000,3px 0 0 #000,0 -3px 0 #000,0 3px 0 #000,0 0 4px #fff,0 0 8px #ffe066,0 0 16px #ffd700,0 0 28px #ffb300,0 0 42px rgba(255,200,0,.85);
                margin-top: 12px;
                position: relative;
                z-index: 2;
            """,
            fontFace = """
                @font-face {
                    font-family: 'Shahrzad';
                    src: url('file:///android_asset/fonts/Shahrzad.ttf');
                }
            """,
            name = "Inverted Purple"
        )
    )

    fun getTheme(index: Int): Theme = themes[index % themes.size]
    val count: Int get() = themes.size

    data class Theme(
        val quranTextStyle: String,
        val translatorStyle: String,
        val infoStyle: String,
        val fontFace: String,
        val name: String
    )

    // ============= انیمیشن‌های ورود آیات (بدون تغییر) =============
    private val animationList = listOf(
        "fadeIn" to """
            @keyframes fadeIn {
                from { opacity: 0; } to { opacity: 1; }
            }
            .fadeIn { animation-name: fadeIn; }
        """,
        "fadeInUp" to """
            @keyframes fadeInUp {
                from { opacity: 0; transform: translateY(30px); }
                to { opacity: 1; transform: translateY(0); }
            }
            .fadeInUp { animation-name: fadeInUp; }
        """,
        "fadeInDown" to """
            @keyframes fadeInDown {
                from { opacity: 0; transform: translateY(-30px); }
                to { opacity: 1; transform: translateY(0); }
            }
            .fadeInDown { animation-name: fadeInDown; }
        """,
        "zoomIn" to """
            @keyframes zoomIn {
                from { opacity: 0; transform: scale(0.8); }
                to { opacity: 1; transform: scale(1); }
            }
            .zoomIn { animation-name: zoomIn; }
        """,
        "slideInRight" to """
            @keyframes slideInRight {
                from { opacity: 0; transform: translateX(50px); }
                to { opacity: 1; transform: translateX(0); }
            }
            .slideInRight { animation-name: slideInRight; }
        """,
        "slideInLeft" to """
            @keyframes slideInLeft {
                from { opacity: 0; transform: translateX(-50px); }
                to { opacity: 1; transform: translateX(0); }
            }
            .slideInLeft { animation-name: slideInLeft; }
        """,
        "bounceIn" to """
            @keyframes bounceIn {
                0% { opacity: 0; transform: scale(0.3); }
                50% { opacity: 1; transform: scale(1.05); }
                70% { transform: scale(0.9); }
                100% { transform: scale(1); }
            }
            .bounceIn { animation-name: bounceIn; }
        """
    )

    val ANIMATION_CSS: String = animationList.joinToString("\n") { it.second }
    fun getRandomAnimationClass(): String = animationList.random().first
}