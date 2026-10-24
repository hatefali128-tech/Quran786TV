package ir.fena.qurantv786_tv

object QuranTheme {
    // -webkit-text-stroke: 2px #393E46;
    private val themes = listOf(
        // 01 — عاجِ سینمایی / Navy Ink
        Theme(
            quranTextStyle = """
                font-size: 42px;
                color: #FFFDF5;
                text-shadow: -2px -2px 0 #111827, 2px -2px 0 #111827, -2px 2px 0 #111827, 2px 2px 0 #111827, -3px 0 0 #111827, 3px 0 0 #111827, 0 -3px 0 #111827, 0 3px 0 #111827, 0 0 5px #FFFDF5, 0 0 12px #D9E7FF, 0 0 22px rgba(125,170,255,.72);
                line-height: 1.9;
                position: relative;
                z-index: 2;
            """,
            translatorStyle = """
                font-size: 32px;
                color: #E8F0FF;
                text-shadow: -1px -1px 0 #172033, 1px -1px 0 #172033, -1px 1px 0 #172033, 1px 1px 0 #172033, 0 0 7px rgba(120,165,255,.72);
                margin-top: 24px;
                position: relative;
                z-index: 2;
            """,
            infoStyle = """
                font-size: 22px;
                color: #C9D8F2;
                text-shadow: -1px -1px 0 #111827, 1px -1px 0 #111827, -1px 1px 0 #111827, 1px 1px 0 #111827, 0 0 6px rgba(125,170,255,.62);
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
            name = "Cinematic Ivory"
        ),

        // 02 — طلای کهربایی / Deep Amber
        Theme(
            quranTextStyle = """
                font-size: 42px;
                color: #FFF7D6;
                text-shadow: -2px -2px 0 #24170A, 2px -2px 0 #24170A, -2px 2px 0 #24170A, 2px 2px 0 #24170A, -3px 0 0 #24170A, 3px 0 0 #24170A, 0 -3px 0 #24170A, 0 3px 0 #24170A, 0 0 4px #FFF8D0, 0 0 10px #FFD166, 0 0 20px #F4A261, 0 0 30px rgba(244,162,97,.58);
                line-height: 1.9;
                position: relative;
                z-index: 2;
            """,
            translatorStyle = """
                font-size: 32px;
                color: #FFE8B0;
                text-shadow: -1px -1px 0 #3A210B, 1px -1px 0 #3A210B, -1px 1px 0 #3A210B, 1px 1px 0 #3A210B, 0 0 7px rgba(255,190,90,.56);
                margin-top: 24px;
                position: relative;
                z-index: 2;
            """,
            infoStyle = """
                font-size: 22px;
                color: #F6C978;
                text-shadow: -1px -1px 0 #24170A, 1px -1px 0 #24170A, -1px 1px 0 #24170A, 1px 1px 0 #24170A, 0 0 6px rgba(255,180,70,.5);
                margin-top: 12px;
                position: relative;
                z-index: 2;
            """,
            fontFace = """
                @font-face {
                    font-family: 'Shahrzad';
                    src: url('file:///android_asset/fonts/Amiri.ttf');
                }
            """,
            name = "Deep Amber"
        ),

        // 03 — یاقوت و شراب / Ruby Wine
        Theme(
            quranTextStyle = """
                font-size: 42px;
                color: #FFF1E8;
                text-shadow: -2px -2px 0 #260D16, 2px -2px 0 #260D16, -2px 2px 0 #260D16, 2px 2px 0 #260D16, -3px 0 0 #260D16, 3px 0 0 #260D16, 0 -3px 0 #260D16, 0 3px 0 #260D16, 0 0 4px #FFE6D5, 0 0 10px #FF6B6B, 0 0 20px #C9184A, 0 0 30px rgba(201,24,74,.62);
                line-height: 1.9;
                position: relative;
                z-index: 2;
            """,
            translatorStyle = """
                font-size: 32px;
                color: #FFD6D6;
                text-shadow: -1px -1px 0 #421321, 1px -1px 0 #421321, -1px 1px 0 #421321, 1px 1px 0 #421321, 0 0 7px rgba(255,100,110,.58);
                margin-top: 24px;
                position: relative;
                z-index: 2;
            """,
            infoStyle = """
                font-size: 22px;
                color: #F3A6AE;
                text-shadow: -1px -1px 0 #260D16, 1px -1px 0 #260D16, -1px 1px 0 #260D16, 1px 1px 0 #260D16, 0 0 6px rgba(220,50,80,.52);
                margin-top: 12px;
                position: relative;
                z-index: 2;
            """,
            fontFace = """
                @font-face {
                    font-family: 'Shahrzad';
                    src: url('file:///android_asset/fonts/King.otf');
                }
            """,
            name = "Ruby Wine"
        ),

        // 04 — فیروزهٔ شب / Teal Aurora
        Theme(
            quranTextStyle = """
                font-size: 42px;
                color: #F2FFFC;
                text-shadow: -2px -2px 0 #062A2A, 2px -2px 0 #062A2A, -2px 2px 0 #062A2A, 2px 2px 0 #062A2A, -3px 0 0 #062A2A, 3px 0 0 #062A2A, 0 -3px 0 #062A2A, 0 3px 0 #062A2A, 0 0 4px #D9FFF7, 0 0 10px #64FFDA, 0 0 20px #00BFA5, 0 0 30px rgba(0,191,165,.58);
                line-height: 1.9;
                position: relative;
                z-index: 2;
            """,
            translatorStyle = """
                font-size: 32px;
                color: #B8F2E6;
                text-shadow: -1px -1px 0 #063A38, 1px -1px 0 #063A38, -1px 1px 0 #063A38, 1px 1px 0 #063A38, 0 0 7px rgba(70,220,190,.6);
                margin-top: 24px;
                position: relative;
                z-index: 2;
            """,
            infoStyle = """
                font-size: 22px;
                color: #7AD8C8;
                text-shadow: -1px -1px 0 #062A2A, 1px -1px 0 #062A2A, -1px 1px 0 #062A2A, 1px 1px 0 #062A2A, 0 0 6px rgba(0,191,165,.5);
                margin-top: 12px;
                position: relative;
                z-index: 2;
            """,
            fontFace = """
                @font-face {
                    font-family: 'Shahrzad';
                    src: url('file:///android_asset/fonts/Noor_Zar.ttf');
                }
            """,
            name = "Teal Aurora"
        ),

        // 05 — آبی الکتریکی / Electric Azure
        Theme(
            quranTextStyle = """
                font-size: 42px;
                color: #F3FBFF;
                text-shadow: -2px -2px 0 #061426, 2px -2px 0 #061426, -2px 2px 0 #061426, 2px 2px 0 #061426, -3px 0 0 #061426, 3px 0 0 #061426, 0 -3px 0 #061426, 0 3px 0 #061426, 0 0 4px #E8FAFF, 0 0 10px #63D8FF, 0 0 20px #00A8E8, 0 0 30px rgba(0,168,232,.58);
                line-height: 1.9;
                position: relative;
                z-index: 2;
            """,
            translatorStyle = """
                font-size: 32px;
                color: #C5EEFF;
                text-shadow: -1px -1px 0 #08283C, 1px -1px 0 #08283C, -1px 1px 0 #08283C, 1px 1px 0 #08283C, 0 0 7px rgba(70,190,255,.58);
                margin-top: 24px;
               
                position: relative;
                z-index: 2;
            """,
            infoStyle = """
                font-size: 22px;
                color: #83D9F5;
                text-shadow: -1px -1px 0 #061426, 1px -1px 0 #061426, -1px 1px 0 #061426, 1px 1px 0 #061426, 0 0 6px rgba(0,168,232,.5);
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
            name = "Electric Azure"
        ),

        // 06 — بنفش آمتیست / Amethyst
        Theme(
            quranTextStyle = """
                font-size: 42px;
                color: #FFF9FF;
                text-shadow: -2px -2px 0 #170D2B, 2px -2px 0 #170D2B, -2px 2px 0 #170D2B, 2px 2px 0 #170D2B, -3px 0 0 #170D2B, 3px 0 0 #170D2B, 0 -3px 0 #170D2B, 0 3px 0 #170D2B, 0 0 4px #F6E8FF, 0 0 10px #D8A7FF, 0 0 20px #8B5CF6, 0 0 30px rgba(139,92,246,.62);
                line-height: 1.9;
                position: relative;
                z-index: 2;
            """,
            translatorStyle = """
                font-size: 32px;
                color: #E8D7FF;
                text-shadow: -1px -1px 0 #25133D, 1px -1px 0 #25133D, -1px 1px 0 #25133D, 1px 1px 0 #25133D, 0 0 7px rgba(170,120,255,.58);
                margin-top: 24px;
                
                position: relative;
                z-index: 2;
            """,
            infoStyle = """
                font-size: 22px;
                color: #C4A6E8;
                text-shadow: -1px -1px 0 #170D2B, 1px -1px 0 #170D2B, -1px 1px 0 #170D2B, 1px 1px 0 #170D2B, 0 0 6px rgba(139,92,246,.5);
                margin-top: 12px;
                position: relative;
                z-index: 2;
            """,
            fontFace = """
                @font-face {
                    font-family: 'Shahrzad';
                    src: url('file:///android_asset/fonts/Amiri.ttf');
                }
            """,
            name = "Amethyst"
        ),

        // 07 — سبز زمردی / Emerald
        Theme(
            quranTextStyle = """
                font-size: 42px;
                color: #F5FFF9;
                text-shadow: -2px -2px 0 #071D16, 2px -2px 0 #071D16, -2px 2px 0 #071D16, 2px 2px 0 #071D16, -3px 0 0 #071D16, 3px 0 0 #071D16, 0 -3px 0 #071D16, 0 3px 0 #071D16, 0 0 4px #E5FFF1, 0 0 10px #7FF2B2, 0 0 20px #18A558, 0 0 30px rgba(24,165,88,.58);
                line-height: 1.9;
                position: relative;
                z-index: 2;
            """,
            translatorStyle = """
                font-size: 32px;
                color: #C5F3D8;
                text-shadow: -1px -1px 0 #0B3828, 1px -1px 0 #0B3828, -1px 1px 0 #0B3828, 1px 1px 0 #0B3828, 0 0 7px rgba(80,220,140,.56);
                margin-top: 24px;
                
                position: relative;
                z-index: 2;
            """,
            infoStyle = """
                font-size: 22px;
                color: #8ED6AB;
                text-shadow: -1px -1px 0 #071D16, 1px -1px 0 #071D16, -1px 1px 0 #071D16, 1px 1px 0 #071D16, 0 0 6px rgba(24,165,88,.5);
                margin-top: 12px;
                position: relative;
                z-index: 2;
            """,
            fontFace = """
                @font-face {
                    font-family: 'Shahrzad';
                    src: url('file:///android_asset/fonts/King.otf');
                }
            """,
            name = "Emerald"
        ),

        // 08 — طلوع مرجانی / Coral Dawn
        Theme(
            quranTextStyle = """
                font-size: 42px;
                color: #FFF9F3;
                text-shadow: -2px -2px 0 #29120D, 2px -2px 0 #29120D, -2px 2px 0 #29120D, 2px 2px 0 #29120D, -3px 0 0 #29120D, 3px 0 0 #29120D, 0 -3px 0 #29120D, 0 3px 0 #29120D, 0 0 4px #FFF1E5, 0 0 10px #FFB38A, 0 0 20px #FF6F61, 0 0 30px rgba(255,111,97,.56);
                line-height: 1.9;
                position: relative;
                z-index: 2;
            """,
            translatorStyle = """
                font-size: 32px;
                color: #FFD8C7;
                text-shadow: -1px -1px 0 #4A1B12, 1px -1px 0 #4A1B12, -1px 1px 0 #4A1B12, 1px 1px 0 #4A1B12, 0 0 7px rgba(255,140,100,.55);
                margin-top: 24px;
                
                position: relative;
                z-index: 2;
            """,
            infoStyle = """
                font-size: 22px;
                color: #F2AE91;
                text-shadow: -1px -1px 0 #29120D, 1px -1px 0 #29120D, -1px 1px 0 #29120D, 1px 1px 0 #29120D, 0 0 6px rgba(255,111,97,.48);
                margin-top: 12px;
                position: relative;
                z-index: 2;
            """,
            fontFace = """
                @font-face {
                    font-family: 'Shahrzad';
                    src: url('file:///android_asset/fonts/Noor_Zar.ttf');
                }
            """,
            name = "Coral Dawn"
        ),

        // 09 — نقره و یخ / Silver Ice
        Theme(
            quranTextStyle = """
                font-size: 42px;
                color: #FFFFFF;
                text-shadow: -2px -2px 0 #10151C, 2px -2px 0 #10151C, -2px 2px 0 #10151C, 2px 2px 0 #10151C, -3px 0 0 #10151C, 3px 0 0 #10151C, 0 -3px 0 #10151C, 0 3px 0 #10151C, 0 0 4px #FFFFFF, 0 0 10px #D7E7F5, 0 0 20px #8FA9BF, 0 0 28px rgba(143,169,191,.5);
                line-height: 1.9;
                position: relative;
                z-index: 2;
            """,
            translatorStyle = """
                font-size: 32px;
                color: #E5EEF5;
                text-shadow: -1px -1px 0 #202A33, 1px -1px 0 #202A33, -1px 1px 0 #202A33, 1px 1px 0 #202A33, 0 0 7px rgba(190,215,235,.5);
                margin-top: 24px;
                
                position: relative;
                z-index: 2;
            """,
            infoStyle = """
                font-size: 22px;
                color: #B9C9D8;
                text-shadow: -1px -1px 0 #10151C, 1px -1px 0 #10151C, -1px 1px 0 #10151C, 1px 1px 0 #10151C, 0 0 6px rgba(143,169,191,.45);
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
            name = "Silver Ice"
        ),

        // 10 — لیمویی نئونی / Lime Signal
        Theme(
            quranTextStyle = """
                font-size: 42px;
                color: #FAFFE8;
                text-shadow: -2px -2px 0 #17200A, 2px -2px 0 #17200A, -2px 2px 0 #17200A, 2px 2px 0 #17200A, -3px 0 0 #17200A, 3px 0 0 #17200A, 0 -3px 0 #17200A, 0 3px 0 #17200A, 0 0 4px #F7FFD1, 0 0 10px #D4FF4F, 0 0 20px #8BC34A, 0 0 30px rgba(139,195,74,.55);
                line-height: 1.9;
                position: relative;
                z-index: 2;
            """,
            translatorStyle = """
                font-size: 32px;
                color: #E5F4B5;
                text-shadow: -1px -1px 0 #26350B, 1px -1px 0 #26350B, -1px 1px 0 #26350B, 1px 1px 0 #26350B, 0 0 7px rgba(180,230,70,.55);
                margin-top: 24px;
                
                position: relative;
                z-index: 2;
            """,
            infoStyle = """
                font-size: 22px;
                color: #B8D978;
                text-shadow: -1px -1px 0 #17200A, 1px -1px 0 #17200A, -1px 1px 0 #17200A, 1px 1px 0 #17200A, 0 0 6px rgba(139,195,74,.48);
                margin-top: 12px;
                position: relative;
                z-index: 2;
            """,
            fontFace = """
                @font-face {
                    font-family: 'Shahrzad';
                    src: url('file:///android_asset/fonts/Amiri.ttf');
                }
            """,
            name = "Lime Signal"
        ),

        // 11 — آبی سلطنتی / Royal Sapphire
        Theme(
            quranTextStyle = """
                font-size: 42px;
                color: #F8FBFF;
                text-shadow: -2px -2px 0 #07112D, 2px -2px 0 #07112D, -2px 2px 0 #07112D, 2px 2px 0 #07112D, -3px 0 0 #07112D, 3px 0 0 #07112D, 0 -3px 0 #07112D, 0 3px 0 #07112D, 0 0 4px #E7EEFF, 0 0 10px #8EA7FF, 0 0 20px #4169E1, 0 0 30px rgba(65,105,225,.58);
                line-height: 1.9;
                position: relative;
                z-index: 2;
            """,
            translatorStyle = """
                font-size: 32px;
                color: #CFD9FF;
                text-shadow: -1px -1px 0 #111E4A, 1px -1px 0 #111E4A, -1px 1px 0 #111E4A, 1px 1px 0 #111E4A, 0 0 7px rgba(110,140,255,.58);
                margin-top: 24px;
                
                position: relative;
                z-index: 2;
            """,
            infoStyle = """
                font-size: 22px;
                color: #A7B7F0;
                text-shadow: -1px -1px 0 #07112D, 1px -1px 0 #07112D, -1px 1px 0 #07112D, 1px 1px 0 #07112D, 0 0 6px rgba(65,105,225,.5);
                margin-top: 12px;
                position: relative;
                z-index: 2;
            """,
            fontFace = """
                @font-face {
                    font-family: 'Shahrzad';
                    src: url('file:///android_asset/fonts/King.otf');
                }
            """,
            name = "Royal Sapphire"
        ),

        // 12 — ارغوانی پلاسما / Plasma Magenta
        Theme(
            quranTextStyle = """
                font-size: 42px;
                color: #FFF7FD;
                text-shadow: -2px -2px 0 #260A22, 2px -2px 0 #260A22, -2px 2px 0 #260A22, 2px 2px 0 #260A22, -3px 0 0 #260A22, 3px 0 0 #260A22, 0 -3px 0 #260A22, 0 3px 0 #260A22, 0 0 4px #FFE8FA, 0 0 10px #FF8BD4, 0 0 20px #D946EF, 0 0 30px rgba(217,70,239,.58);
                line-height: 1.9;
                position: relative;
                z-index: 2;
            """,
            translatorStyle = """
                font-size: 32px;
                color: #F4C6EA;
                text-shadow: -1px -1px 0 #461536, 1px -1px 0 #461536, -1px 1px 0 #461536, 1px 1px 0 #461536, 0 0 7px rgba(220,100,220,.58);
                margin-top: 24px;
                
                position: relative;
                z-index: 2;
            """,
            infoStyle = """
                font-size: 22px;
                color: #D89BCF;
                text-shadow: -1px -1px 0 #260A22, 1px -1px 0 #260A22, -1px 1px 0 #260A22, 1px 1px 0 #260A22, 0 0 6px rgba(217,70,239,.5);
                margin-top: 12px;
                position: relative;
                z-index: 2;
            """,
            fontFace = """
                @font-face {
                    font-family: 'Shahrzad';
                    src: url('file:///android_asset/fonts/Noor_Zar.ttf');
                }
            """,
            name = "Plasma Magenta"
        ),

        // 13 — طلای سفید / Champagne Gold
        Theme(
            quranTextStyle = """
                font-size: 42px;
                color: #FFFEF8;
                text-shadow: -2px -2px 0 #211A0D, 2px -2px 0 #211A0D, -2px 2px 0 #211A0D, 2px 2px 0 #211A0D, -3px 0 0 #211A0D, 3px 0 0 #211A0D, 0 -3px 0 #211A0D, 0 3px 0 #211A0D, 0 0 4px #FFFFFF, 0 0 9px #FFE8A3, 0 0 18px #E7B94C, 0 0 28px rgba(231,185,76,.56);
                line-height: 1.9;
                position: relative;
                z-index: 2;
            """,
            translatorStyle = """
                font-size: 32px;
                color: #F8E4B0;
                text-shadow: -1px -1px 0 #3B2A0F, 1px -1px 0 #3B2A0F, -1px 1px 0 #3B2A0F, 1px 1px 0 #3B2A0F, 0 0 7px rgba(220,180,90,.54);
                margin-top: 24px;
                
                position: relative;
                z-index: 2;
            """,
            infoStyle = """
                font-size: 22px;
                color: #D6B86A;
                text-shadow: -1px -1px 0 #211A0D, 1px -1px 0 #211A0D, -1px 1px 0 #211A0D, 1px 1px 0 #211A0D, 0 0 6px rgba(231,185,76,.48);
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
            name = "Champagne Gold"
        ),

        // 14 — سفید نئون / Arctic Neon
        Theme(
            quranTextStyle = """
                font-size: 42px;
                color: #FFFFFF;
                text-shadow: -2px -2px 0 #050505, 2px -2px 0 #050505, -2px 2px 0 #050505, 2px 2px 0 #050505, -3px 0 0 #050505, 3px 0 0 #050505, 0 -3px 0 #050505, 0 3px 0 #050505, 0 0 4px #FFFFFF, 0 0 9px #E8F7FF, 0 0 18px #A7E8FF, 0 0 28px rgba(90,210,255,.62);
                line-height: 1.9;
                position: relative;
                z-index: 2;
            """,
            translatorStyle = """
                font-size: 32px;
                color: #DDF6FF;
                text-shadow: -1px -1px 0 #12232B, 1px -1px 0 #12232B, -1px 1px 0 #12232B, 1px 1px 0 #12232B, 0 0 7px rgba(120,220,255,.56);
                margin-top: 24px;
                
                position: relative;
                z-index: 2;
            """,
            infoStyle = """
                font-size: 22px;
                color: #A9D8E8;
                text-shadow: -1px -1px 0 #050505, 1px -1px 0 #050505, -1px 1px 0 #050505, 1px 1px 0 #050505, 0 0 6px rgba(90,210,255,.5);
                margin-top: 12px;
                position: relative;
                z-index: 2;
            """,
            fontFace = """
                @font-face {
                    font-family: 'Shahrzad';
                    src: url('file:///android_asset/fonts/Amiri.ttf');
                }
            """,
            name = "Arctic Neon"
        ),



    // 05 — یاقوت سلطنتی / Ruby Velvet
    Theme(
    quranTextStyle = """
            font-size: 42px;
            color: #FFF5F7;
            text-shadow: -2px -2px 0 #3A0715, 2px -2px 0 #3A0715, -2px 2px 0 #3A0715, 2px 2px 0 #3A0715, -3px 0 0 #3A0715, 3px 0 0 #3A0715, 0 -3px 0 #3A0715, 0 3px 0 #3A0715, 0 0 4px #FFE1E8, 0 0 10px #FF6B8A, 0 0 20px #D72652, 0 0 30px rgba(215,38,82,.58);
            line-height: 1.9;
            position: relative;
            z-index: 2;
        """,
    translatorStyle = """
            font-size: 32px;
            color: #F4B8C5;
            text-shadow: -1px -1px 0 #4A0A1B, 1px -1px 0 #4A0A1B, -1px 1px 0 #4A0A1B, 1px 1px 0 #4A0A1B, 0 0 7px rgba(255,95,130,.6);
            margin-top: 24px;
            position: relative;
            z-index: 2;
        """,
    infoStyle = """
            font-size: 22px;
            color: #D98A9D;
            text-shadow: -1px -1px 0 #3A0715, 1px -1px 0 #3A0715, -1px 1px 0 #3A0715, 1px 1px 0 #3A0715, 0 0 6px rgba(215,38,82,.5);
            margin-top: 12px;
            position: relative;
            z-index: 2;
        """,
    fontFace = """
            @font-face {
                font-family: 'Shahrzad';
                src: url('file:///android_asset/fonts/Noor_Zar.ttf');
            }
        """,
    name = "Ruby Velvet"
    ),

    // 06 — آبی یخی / Arctic Sapphire
    Theme(
    quranTextStyle = """
            font-size: 42px;
            color: #F4FBFF;
            text-shadow: -2px -2px 0 #061B32, 2px -2px 0 #061B32, -2px 2px 0 #061B32, 2px 2px 0 #061B32, -3px 0 0 #061B32, 3px 0 0 #061B32, 0 -3px 0 #061B32, 0 3px 0 #061B32, 0 0 4px #D9F4FF, 0 0 10px #64C7FF, 0 0 20px #1687D9, 0 0 30px rgba(22,135,217,.58);
            line-height: 1.9;
            position: relative;
            z-index: 2;
        """,
    translatorStyle = """
            font-size: 32px;
            color: #B5DDF2;
            text-shadow: -1px -1px 0 #082744, 1px -1px 0 #082744, -1px 1px 0 #082744, 1px 1px 0 #082744, 0 0 7px rgba(80,185,245,.6);
            margin-top: 24px;
            position: relative;
            z-index: 2;
        """,
    infoStyle = """
            font-size: 22px;
            color: #79BBD8;
            text-shadow: -1px -1px 0 #061B32, 1px -1px 0 #061B32, -1px 1px 0 #061B32, 1px 1px 0 #061B32, 0 0 6px rgba(22,135,217,.5);
            margin-top: 12px;
            position: relative;
            z-index: 2;
        """,
    fontFace = """
            @font-face {
                font-family: 'Shahrzad';
                src: url('file:///android_asset/fonts/Noor_Zar.ttf');
            }
        """,
    name = "Arctic Sapphire"
    ),

    // 07 — بنفش آمیتیست / Amethyst Night
    Theme(
    quranTextStyle = """
            font-size: 42px;
            color: #FBF7FF;
            text-shadow: -2px -2px 0 #190B2B, 2px -2px 0 #190B2B, -2px 2px 0 #190B2B, 2px 2px 0 #190B2B, -3px 0 0 #190B2B, 3px 0 0 #190B2B, 0 -3px 0 #190B2B, 0 3px 0 #190B2B, 0 0 4px #EAD9FF, 0 0 10px #C084FC, 0 0 20px #7C3AED, 0 0 30px rgba(124,58,237,.58);
            line-height: 1.9;
            position: relative;
            z-index: 2;
        """,
    translatorStyle = """
            font-size: 32px;
            color: #DCC8F2;
            text-shadow: -1px -1px 0 #26113D, 1px -1px 0 #26113D, -1px 1px 0 #26113D, 1px 1px 0 #26113D, 0 0 7px rgba(180,115,245,.6);
            margin-top: 24px;
            position: relative;
            z-index: 2;
        """,
    infoStyle = """
            font-size: 22px;
            color: #B99BD5;
            text-shadow: -1px -1px 0 #190B2B, 1px -1px 0 #190B2B, -1px 1px 0 #190B2B, 1px 1px 0 #190B2B, 0 0 6px rgba(124,58,237,.5);
            margin-top: 12px;
            position: relative;
            z-index: 2;
        """,
    fontFace = """
            @font-face {
                font-family: 'Shahrzad';
                src: url('file:///android_asset/fonts/Noor_Zar.ttf');
            }
        """,
    name = "Amethyst Night"
    ),

    // 08 — طلایی کهربایی / Amber Crown
    Theme(
    quranTextStyle = """
            font-size: 42px;
            color: #FFFBEA;
            text-shadow: -2px -2px 0 #382000, 2px -2px 0 #382000, -2px 2px 0 #382000, 2px 2px 0 #382000, -3px 0 0 #382000, 3px 0 0 #382000, 0 -3px 0 #382000, 0 3px 0 #382000, 0 0 4px #FFF3B0, 0 0 10px #FFD166, 0 0 20px #F59E0B, 0 0 30px rgba(245,158,11,.58);
            line-height: 1.9;
            position: relative;
            z-index: 2;
        """,
    translatorStyle = """
            font-size: 32px;
            color: #F5D99A;
            text-shadow: -1px -1px 0 #4A2A00, 1px -1px 0 #4A2A00, -1px 1px 0 #4A2A00, 1px 1px 0 #4A2A00, 0 0 7px rgba(255,190,65,.6);
            margin-top: 24px;
            position: relative;
            z-index: 2;
        """,
    infoStyle = """
            font-size: 22px;
            color: #D9B96E;
            text-shadow: -1px -1px 0 #382000, 1px -1px 0 #382000, -1px 1px 0 #382000, 1px 1px 0 #382000, 0 0 6px rgba(245,158,11,.5);
            margin-top: 12px;
            position: relative;
            z-index: 2;
        """,
    fontFace = """
            @font-face {
                font-family: 'Shahrzad';
                src: url('file:///android_asset/fonts/Noor_Zar.ttf');
            }
        """,
    name = "Amber Crown"
    ),

    // 09 — فیروزه‌ای اقیانوسی / Ocean Turquoise
    Theme(
    quranTextStyle = """
            font-size: 42px;
            color: #F0FFFE;
            text-shadow: -2px -2px 0 #032C35, 2px -2px 0 #032C35, -2px 2px 0 #032C35, 2px 2px 0 #032C35, -3px 0 0 #032C35, 3px 0 0 #032C35, 0 -3px 0 #032C35, 0 3px 0 #032C35, 0 0 4px #C7FFFA, 0 0 10px #4DEEEA, 0 0 20px #00A6A6, 0 0 30px rgba(0,166,166,.58);
            line-height: 1.9;
            position: relative;
            z-index: 2;
        """,
    translatorStyle = """
            font-size: 32px;
            color: #A8E8E4;
            text-shadow: -1px -1px 0 #06404A, 1px -1px 0 #06404A, -1px 1px 0 #06404A, 1px 1px 0 #06404A, 0 0 7px rgba(60,220,215,.6);
            margin-top: 24px;
            position: relative;
            z-index: 2;
        """,
    infoStyle = """
            font-size: 22px;
            color: #70C8C6;
            text-shadow: -1px -1px 0 #032C35, 1px -1px 0 #032C35, -1px 1px 0 #032C35, 1px 1px 0 #032C35, 0 0 6px rgba(0,166,166,.5);
            margin-top: 12px;
            position: relative;
            z-index: 2;
        """,
    fontFace = """
            @font-face {
                font-family: 'Shahrzad';
                src: url('file:///android_asset/fonts/Noor_Zar.ttf');
            }
        """,
    name = "Ocean Turquoise"
    ),

    // 10 — رزگلد / Rose Gold
    Theme(
    quranTextStyle = """
            font-size: 42px;
            color: #FFF7F5;
            text-shadow: -2px -2px 0 #35141B, 2px -2px 0 #35141B, -2px 2px 0 #35141B, 2px 2px 0 #35141B, -3px 0 0 #35141B, 3px 0 0 #35141B, 0 -3px 0 #35141B, 0 3px 0 #35141B, 0 0 4px #FFE2D6, 0 0 10px #FF9F8A, 0 0 20px #E76F51, 0 0 30px rgba(231,111,81,.58);
            line-height: 1.9;
            position: relative;
            z-index: 2;
        """,
    translatorStyle = """
            font-size: 32px;
            color: #F2C1B6;
            text-shadow: -1px -1px 0 #4A1E21, 1px -1px 0 #4A1E21, -1px 1px 0 #4A1E21, 1px 1px 0 #4A1E21, 0 0 7px rgba(245,135,110,.6);
            margin-top: 24px;
            position: relative;
            z-index: 2;
        """,
    infoStyle = """
            font-size: 22px;
            color: #D9978A;
            text-shadow: -1px -1px 0 #35141B, 1px -1px 0 #35141B, -1px 1px 0 #35141B, 1px 1px 0 #35141B, 0 0 6px rgba(231,111,81,.5);
            margin-top: 12px;
            position: relative;
            z-index: 2;
        """,
    fontFace = """
            @font-face {
                font-family: 'Shahrzad';
                src: url('file:///android_asset/fonts/Noor_Zar.ttf');
            }
        """,
    name = "Rose Gold"
    ),

    // 11 — سبز زمردی / Emerald Temple
    Theme(
    quranTextStyle = """
            font-size: 42px;
            color: #F3FFF8;
            text-shadow: -2px -2px 0 #062B1B, 2px -2px 0 #062B1B, -2px 2px 0 #062B1B, 2px 2px 0 #062B1B, -3px 0 0 #062B1B, 3px 0 0 #062B1B, 0 -3px 0 #062B1B, 0 3px 0 #062B1B, 0 0 4px #D4FFE6, 0 0 10px #5CFF9D, 0 0 20px #00A86B, 0 0 30px rgba(0,168,107,.58);
            line-height: 1.9;
            position: relative;
            z-index: 2;
        """,
    translatorStyle = """
            font-size: 32px;
            color: #AFE5C5;
            text-shadow: -1px -1px 0 #083D28, 1px -1px 0 #083D28, -1px 1px 0 #083D28, 1px 1px 0 #083D28, 0 0 7px rgba(75,220,135,.6);
            margin-top: 24px;
            position: relative;
            z-index: 2;
        """,
    infoStyle = """
            font-size: 22px;
            color: #76C49A;
            text-shadow: -1px -1px 0 #062B1B, 1px -1px 0 #062B1B, -1px 1px 0 #062B1B, 1px 1px 0 #062B1B, 0 0 6px rgba(0,168,107,.5);
            margin-top: 12px;
            position: relative;
            z-index: 2;
        """,
    fontFace = """
            @font-face {
                font-family: 'Shahrzad';
                src: url('file:///android_asset/fonts/Noor_Zar.ttf');
            }
        """,
    name = "Emerald Temple"
    ),

    // 12 — لاجوردی سلطنتی / Royal Cobalt
    Theme(
    quranTextStyle = """
            font-size: 42px;
            color: #F5F8FF;
            text-shadow: -2px -2px 0 #071536, 2px -2px 0 #071536, -2px 2px 0 #071536, 2px 2px 0 #071536, -3px 0 0 #071536, 3px 0 0 #071536, 0 -3px 0 #071536, 0 3px 0 #071536, 0 0 4px #DCE7FF, 0 0 10px #6E8CFF, 0 0 20px #3155D9, 0 0 30px rgba(49,85,217,.58);
            line-height: 1.9;
            position: relative;
            z-index: 2;
        """,
    translatorStyle = """
            font-size: 32px;
            color: #B8C8F0;
            text-shadow: -1px -1px 0 #0C2050, 1px -1px 0 #0C2050, -1px 1px 0 #0C2050, 1px 1px 0 #0C2050, 0 0 7px rgba(100,130,245,.6);
            margin-top: 24px;
            position: relative;
            z-index: 2;
        """,
    infoStyle = """
            font-size: 22px;
            color: #8298D2;
            text-shadow: -1px -1px 0 #071536, 1px -1px 0 #071536, -1px 1px 0 #071536, 1px 1px 0 #071536, 0 0 6px rgba(49,85,217,.5);
            margin-top: 12px;
            position: relative;
            z-index: 2;
        """,
    fontFace = """
            @font-face {
                font-family: 'Shahrzad';
                src: url('file:///android_asset/fonts/Noor_Zar.ttf');
            }
        """,
    name = "Royal Cobalt"
    ),

    // 13 — نیون لیمویی / Electric Lime
    Theme(
    quranTextStyle = """
            font-size: 42px;
            color: #FAFFE8;
            text-shadow: -2px -2px 0 #202B05, 2px -2px 0 #202B05, -2px 2px 0 #202B05, 2px 2px 0 #202B05, -3px 0 0 #202B05, 3px 0 0 #202B05, 0 -3px 0 #202B05, 0 3px 0 #202B05, 0 0 4px #F0FFB0, 0 0 10px #D4FF4F, 0 0 20px #8DB600, 0 0 30px rgba(141,182,0,.58);
            line-height: 1.9;
            position: relative;
            z-index: 2;
        """,
    translatorStyle = """
            font-size: 32px;
            color: #D4E99A;
            text-shadow: -1px -1px 0 #303D08, 1px -1px 0 #303D08, -1px 1px 0 #303D08, 1px 1px 0 #303D08, 0 0 7px rgba(195,235,70,.6);
            margin-top: 24px;
            position: relative;
            z-index: 2;
        """,
    infoStyle = """
            font-size: 22px;
            color: #AFC56A;
            text-shadow: -1px -1px 0 #202B05, 1px -1px 0 #202B05, -1px 1px 0 #202B05, 1px 1px 0 #202B05, 0 0 6px rgba(141,182,0,.5);
            margin-top: 12px;
            position: relative;
            z-index: 2;
        """,
    fontFace = """
            @font-face {
                font-family: 'Shahrzad';
                src: url('file:///android_asset/fonts/Noor_Zar.ttf');
            }
        """,
    name = "Electric Lime"
    ),

    // 14 — یخی بنفش / Lavender Ice
    Theme(
    quranTextStyle = """
            font-size: 42px;
            color: #FCFAFF;
            text-shadow: -2px -2px 0 #21152F, 2px -2px 0 #21152F, -2px 2px 0 #21152F, 2px 2px 0 #21152F, -3px 0 0 #21152F, 3px 0 0 #21152F, 0 -3px 0 #21152F, 0 3px 0 #21152F, 0 0 4px #F1E8FF, 0 0 10px #D7B8FF, 0 0 20px #A56BDB, 0 0 30px rgba(165,107,219,.58);
            line-height: 1.9;
            position: relative;
            z-index: 2;
        """,
    translatorStyle = """
            font-size: 32px;
            color: #D8C8E8;
            text-shadow: -1px -1px 0 #30203F, 1px -1px 0 #30203F, -1px 1px 0 #30203F, 1px 1px 0 #30203F, 0 0 7px rgba(190,145,235,.6);
            margin-top: 24px;
            position: relative;
            z-index: 2;
        """,
    infoStyle = """
            font-size: 22px;
            color: #B69ACD;
            text-shadow: -1px -1px 0 #21152F, 1px -1px 0 #21152F, -1px 1px 0 #21152F, 1px 1px 0 #21152F, 0 0 6px rgba(165,107,219,.5);
            margin-top: 12px;
            position: relative;
            z-index: 2;
        """,
    fontFace = """
            @font-face {
                font-family: 'Shahrzad';
                src: url('file:///android_asset/fonts/Noor_Zar.ttf');
            }
        """,
    name = "Lavender Ice"
    ),

    // 15 — نارنجی آتشین / Solar Ember
    Theme(
    quranTextStyle = """
            font-size: 42px;
            color: #FFF9F0;
            text-shadow: -2px -2px 0 #3A1204, 2px -2px 0 #3A1204, -2px 2px 0 #3A1204, 2px 2px 0 #3A1204, -3px 0 0 #3A1204, 3px 0 0 #3A1204, 0 -3px 0 #3A1204, 0 3px 0 #3A1204, 0 0 4px #FFE4C4, 0 0 10px #FFB347, 0 0 20px #F05A28, 0 0 30px rgba(240,90,40,.58);
            line-height: 1.9;
            position: relative;
            z-index: 2;
        """,
    translatorStyle = """
            font-size: 32px;
            color: #F4C49B;
            text-shadow: -1px -1px 0 #51200A, 1px -1px 0 #51200A, -1px 1px 0 #51200A, 1px 1px 0 #51200A, 0 0 7px rgba(245,145,70,.6);
            margin-top: 24px;
            position: relative;
            z-index: 2;
        """,
    infoStyle = """
            font-size: 22px;
            color: #D99B6B;
            text-shadow: -1px -1px 0 #3A1204, 1px -1px 0 #3A1204, -1px 1px 0 #3A1204, 1px 1px 0 #3A1204, 0 0 6px rgba(240,90,40,.5);
            margin-top: 12px;
            position: relative;
            z-index: 2;
        """,
    fontFace = """
            @font-face {
                font-family: 'Shahrzad';
                src: url('file:///android_asset/fonts/Noor_Zar.ttf');
            }
        """,
    name = "Solar Ember"
    ),

    // 16 — سفید مرواریدی / Pearl Moon
    Theme(
    quranTextStyle = """
            font-size: 42px;
            color: #FFFFFF;
            text-shadow: -2px -2px 0 #181B24, 2px -2px 0 #181B24, -2px 2px 0 #181B24, 2px 2px 0 #181B24, -3px 0 0 #181B24, 3px 0 0 #181B24, 0 -3px 0 #181B24, 0 3px 0 #181B24, 0 0 4px #FFFFFF, 0 0 10px #D6E4FF, 0 0 20px #9DB7E8, 0 0 30px rgba(157,183,232,.55);
            line-height: 1.9;
            position: relative;
            z-index: 2;
        """,
    translatorStyle = """
            font-size: 32px;
            color: #D6DDEC;
            text-shadow: -1px -1px 0 #2C3140, 1px -1px 0 #2C3140, -1px 1px 0 #2C3140, 1px 1px 0 #2C3140, 0 0 7px rgba(180,200,235,.55);
            margin-top: 24px;
            position: relative;
            z-index: 2;
        """,
    infoStyle = """
            font-size: 22px;
            color: #AAB4C9;
            text-shadow: -1px -1px 0 #181B24, 1px -1px 0 #181B24, -1px 1px 0 #181B24, 1px 1px 0 #181B24, 0 0 6px rgba(157,183,232,.45);
            margin-top: 12px;
            position: relative;
            z-index: 2;
        """,
    fontFace = """
            @font-face {
                font-family: 'Shahrzad';
                src: url('file:///android_asset/fonts/Noor_Zar.ttf');
            }
        """,
    name = "Pearl Moon"
    ),

    // 17 — آبی-سبز سایبری / Cyber Mint
    Theme(
    quranTextStyle = """
            font-size: 42px;
            color: #F2FFFF;
            text-shadow: -2px -2px 0 #06242A, 2px -2px 0 #06242A, -2px 2px 0 #06242A, 2px 2px 0 #06242A, -3px 0 0 #06242A, 3px 0 0 #06242A, 0 -3px 0 #06242A, 0 3px 0 #06242A, 0 0 4px #D9FFFF, 0 0 10px #58F6E8, 0 0 20px #00B8B0, 0 0 30px rgba(0,184,176,.58);
            line-height: 1.9;
            position: relative;
            z-index: 2;
        """,
    translatorStyle = """
            font-size: 32px;
            color: #A8E8E4;
            text-shadow: -1px -1px 0 #073A40, 1px -1px 0 #073A40, -1px 1px 0 #073A40, 1px 1px 0 #073A40, 0 0 7px rgba(65,220,210,.6);
            margin-top: 24px;
            position: relative;
            z-index: 2;
        """,
    infoStyle = """
            font-size: 22px;
            color: #70C7C2;
            text-shadow: -1px -1px 0 #06242A, 1px -1px 0 #06242A, -1px 1px 0 #06242A, 1px 1px 0 #06242A, 0 0 6px rgba(0,184,176,.5);
            margin-top: 12px;
            position: relative;
            z-index: 2;
        """,
    fontFace = """
            @font-face {
                font-family: 'Shahrzad';
                src: url('file:///android_asset/fonts/Noor_Zar.ttf');
            }
        """,
    name = "Cyber Mint"
    ),

    // 18 — صورتی نئون / Neon Fuchsia
    Theme(
    quranTextStyle = """
            font-size: 42px;
            color: #FFF5FC;
            text-shadow: -2px -2px 0 #350A2A, 2px -2px 0 #350A2A, -2px 2px 0 #350A2A, 2px 2px 0 #350A2A, -3px 0 0 #350A2A, 3px 0 0 #350A2A, 0 -3px 0 #350A2A, 0 3px 0 #350A2A, 0 0 4px #FFD9F2, 0 0 10px #FF70D4, 0 0 20px #D946B8, 0 0 30px rgba(217,70,184,.58);
            line-height: 1.9;
            position: relative;
            z-index: 2;
        """,
    translatorStyle = """
            font-size: 32px;
            color: #EAB4D9;
            text-shadow: -1px -1px 0 #4A123C, 1px -1px 0 #4A123C, -1px 1px 0 #4A123C, 1px 1px 0 #4A123C, 0 0 7px rgba(240,105,205,.6);
            margin-top: 24px;
            position: relative;
            z-index: 2;
        """,
    infoStyle = """
            font-size: 22px;
            color: #C785B8;
            text-shadow: -1px -1px 0 #350A2A, 1px -1px 0 #350A2A, -1px 1px 0 #350A2A, 1px 1px 0 #350A2A, 0 0 6px rgba(217,70,184,.5);
            margin-top: 12px;
            position: relative;
            z-index: 2;
        """,
    fontFace = """
            @font-face {
                font-family: 'Shahrzad';
                src: url('file:///android_asset/fonts/Noor_Zar.ttf');
            }
        """,
    name = "Neon Fuchsia"
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

    // ============= ??????????? ???? (???? ?????) =============
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