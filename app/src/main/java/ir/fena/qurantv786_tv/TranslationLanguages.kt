package ir.fena.qurantv786_tv

/**
 * فهرست زبان‌های قابل ترجمه با Google Translate.
 * مبنا: ترجمهٔ فارسی (sl=fa).
 * فرمت هر خط:  کد|نام بومی|نام انگلیسی|کد کشور برای پرچم
 */
data class TranslationLanguage(
    val code: String,
    val name: String,
    val englishName: String,
    val country: String
) {
    /** پرچم از روی کد کشور ساخته می‌شود (Regional Indicator) */
    val flag: String
        get() {
            if (country.length != 2) return "🏳️"
            val a = Character.toChars(0x1F1E6 + (country[0].uppercaseChar() - 'A'))
            val b = Character.toChars(0x1F1E6 + (country[1].uppercaseChar() - 'A'))
            return String(a) + String(b)
        }

    val isRtl: Boolean
        get() = code in RTL_CODES

    companion object {
        private val RTL_CODES = setOf(
            "fa", "ar", "ur", "iw", "yi", "ps", "ku", "ckb", "sd", "ug", "dv", "bal", "ms-Arab", "nqo"
        )

        private const val RAW = """
fa|فارسی|Persian|IR
en|English|English|GB
ar|العربية|Arabic|SA
tr|Türkçe|Turkish|TR
ur|اردو|Urdu|PK
fr|Français|French|FR
de|Deutsch|German|DE
es|Español|Spanish|ES
it|Italiano|Italian|IT
ru|Русский|Russian|RU
zh-CN|简体中文|Chinese (Simplified)|CN
zh-TW|繁體中文|Chinese (Traditional)|TW
yue|粵語|Cantonese|HK
ja|日本語|Japanese|JP
ko|한국어|Korean|KR
hi|हिन्दी|Hindi|IN
id|Bahasa Indonesia|Indonesian|ID
ms|Bahasa Melayu|Malay|MY
ms-Arab|ملايو (جاوي)|Malay (Jawi)|BN
bn|বাংলা|Bengali|BD
az|Azərbaycan|Azerbaijani|AZ
nl|Nederlands|Dutch|NL
pt|Português|Portuguese (Brazil)|BR
pt-PT|Português (Portugal)|Portuguese (Portugal)|PT
sw|Kiswahili|Swahili|KE
ha|Hausa|Hausa|NG
am|አማርኛ|Amharic|ET
so|Soomaali|Somali|SO
tg|Тоҷикӣ|Tajik|TJ
ps|پښتو|Pashto|AF
ku|Kurdî (Kurmancî)|Kurdish (Kurmanji)|TR
ckb|کوردی سۆرانی|Kurdish (Sorani)|IQ
af|Afrikaans|Afrikaans|ZA
sq|Shqip|Albanian|AL
hy|Հայերեն|Armenian|AM
as|অসমীয়া|Assamese|IN
ay|Aymar aru|Aymara|BO
bm|Bamanankan|Bambara|ML
eu|Euskara|Basque|ES
be|Беларуская|Belarusian|BY
bho|भोजपुरी|Bhojpuri|IN
bs|Bosanski|Bosnian|BA
bg|Български|Bulgarian|BG
ca|Català|Catalan|AD
ceb|Cebuano|Cebuano|PH
co|Corsu|Corsican|FR
hr|Hrvatski|Croatian|HR
cs|Čeština|Czech|CZ
da|Dansk|Danish|DK
dv|ދިވެހި|Dhivehi|MV
doi|डोगरी|Dogri|IN
eo|Esperanto|Esperanto|EU
et|Eesti|Estonian|EE
ee|Eʋegbe|Ewe|GH
fil|Filipino|Filipino|PH
fi|Suomi|Finnish|FI
fy|Frysk|Frisian|NL
gl|Galego|Galician|ES
ka|ქართული|Georgian|GE
el|Ελληνικά|Greek|GR
gn|Avañe'ẽ|Guarani|PY
gu|ગુજરાતી|Gujarati|IN
ht|Kreyòl Ayisyen|Haitian Creole|HT
haw|ʻŌlelo Hawaiʻi|Hawaiian|US
iw|עברית|Hebrew|IL
hmn|Hmoob|Hmong|LA
hu|Magyar|Hungarian|HU
is|Íslenska|Icelandic|IS
ig|Igbo|Igbo|NG
ilo|Ilokano|Ilocano|PH
ga|Gaeilge|Irish|IE
jw|Basa Jawa|Javanese|ID
kn|ಕನ್ನಡ|Kannada|IN
kk|Қазақша|Kazakh|KZ
km|ខ្មែរ|Khmer|KH
rw|Kinyarwanda|Kinyarwanda|RW
gom|कोंकणी|Konkani|IN
kri|Krio|Krio|SL
ky|Кыргызча|Kyrgyz|KG
lo|ລາວ|Lao|LA
la|Latina|Latin|VA
lv|Latviešu|Latvian|LV
ln|Lingála|Lingala|CD
lt|Lietuvių|Lithuanian|LT
lg|Luganda|Luganda|UG
lb|Lëtzebuergesch|Luxembourgish|LU
mk|Македонски|Macedonian|MK
mai|मैथिली|Maithili|IN
mg|Malagasy|Malagasy|MG
ml|മലയാളം|Malayalam|IN
mt|Malti|Maltese|MT
mi|Māori|Maori|NZ
mr|मराठी|Marathi|IN
mni-Mtei|ꯃꯅꯤꯄꯨꯔꯤ|Meiteilon (Manipuri)|IN
lus|Mizo ṭawng|Mizo|IN
mn|Монгол|Mongolian|MN
my|မြန်မာ|Myanmar (Burmese)|MM
ne|नेपाली|Nepali|NP
no|Norsk|Norwegian|NO
ny|Chichewa|Chichewa|MW
or|ଓଡ଼ିଆ|Odia (Oriya)|IN
om|Afaan Oromoo|Oromo|ET
pl|Polski|Polish|PL
pa|ਪੰਜਾਬੀ|Punjabi|IN
qu|Runasimi|Quechua|PE
ro|Română|Romanian|RO
sm|Gagana Samoa|Samoan|WS
sa|संस्कृतम्|Sanskrit|IN
gd|Gàidhlig|Scots Gaelic|GB
nso|Sepedi|Sepedi|ZA
sr|Српски|Serbian|RS
st|Sesotho|Sesotho|LS
sn|chiShona|Shona|ZW
sd|سنڌي|Sindhi|PK
si|සිංහල|Sinhala|LK
sk|Slovenčina|Slovak|SK
sl|Slovenščina|Slovenian|SI
su|Basa Sunda|Sundanese|ID
sv|Svenska|Swedish|SE
ta|தமிழ்|Tamil|IN
tt|Татарча|Tatar|RU
te|తెలుగు|Telugu|IN
th|ไทย|Thai|TH
ti|ትግርኛ|Tigrinya|ER
ts|Xitsonga|Tsonga|ZA
tk|Türkmençe|Turkmen|TM
ak|Twi|Twi (Akan)|GH
uk|Українська|Ukrainian|UA
ug|ئۇيغۇرچە|Uyghur|CN
uz|Oʻzbekcha|Uzbek|UZ
vi|Tiếng Việt|Vietnamese|VN
cy|Cymraeg|Welsh|GB
xh|isiXhosa|Xhosa|ZA
yi|ייִדיש|Yiddish|IL
yo|Yorùbá|Yoruba|NG
zu|isiZulu|Zulu|ZA
ab|Аҧсуа|Abkhaz|GE
ace|Bahsa Acèh|Acehnese|ID
ach|Acholi|Acholi|UG
alz|Alur|Alur|CD
awa|अवधी|Awadhi|IN
ban|Basa Bali|Balinese|ID
bal|بلوچی|Baluchi|PK
ba|Башҡортса|Bashkir|RU
btx|Batak Karo|Batak Karo|ID
bts|Batak Simalungun|Batak Simalungun|ID
bbc|Batak Toba|Batak Toba|ID
bew|Betawi|Betawi|ID
bik|Bikol|Bikol|PH
br|Brezhoneg|Breton|FR
bua|Буряад|Buryat|RU
cv|Чӑвашла|Chuvash|RU
crh|Qırımtatarca|Crimean Tatar|UA
din|Thuɔŋjäŋ|Dinka|SS
dov|Dombe|Dombe|ZW
dz|རྫོང་ཁ|Dzongkha|BT
fo|Føroyskt|Faroese|FO
fj|Na Vosa Vakaviti|Fijian|FJ
fon|Fɔngbè|Fon|BJ
fur|Furlan|Friulian|IT
gaa|Ga|Ga|GH
cnh|Hakha Chin|Hakha Chin|MM
hil|Hiligaynon|Hiligaynon|PH
hrx|Hunsrik|Hunsrik|BR
iba|Iban|Iban|MY
kl|Kalaallisut|Kalaallisut (Greenlandic)|GL
pam|Kapampangan|Kapampangan|PH
cgg|Kiga|Kiga|UG
ktu|Kituba|Kituba|CD
trp|Kokborok|Kokborok|IN
kv|Коми|Komi|RU
ltg|Latgaļu|Latgalian|LV
lij|Ligure|Ligurian|IT
li|Limburgs|Limburgish|NL
luo|Dholuo|Luo|KE
mad|Madhura|Madurese|ID
mak|Makassar|Makassar|ID
gv|Gaelg|Manx|IM
mh|Kajin M̧ajeļ|Marshallese|MH
mwr|मारवाड़ी|Marwadi|IN
chm|Марий|Meadow Mari|RU
min|Baso Minang|Minang|ID
nqo|ߒߞߏ|NKo|GN
new|नेपाल भाषा|Nepalbhasa (Newari)|NP
nus|Thok Naath|Nuer|SS
oc|Occitan|Occitan|FR
pag|Pangasinan|Pangasinan|PH
pap|Papiamento|Papiamento|AW
kek|Qʼeqchiʼ|Qʼeqchiʼ|GT
rom|Romani|Romani|RO
rn|Ikirundi|Rundi|BI
sg|Sängö|Sango|CF
sat|ᱥᱟᱱᱛᱟᱲᱤ|Santali|IN
crs|Kreol Seselwa|Seychellois Creole|SC
shn|ၽႃႇသႃႇတႆး|Shan|MM
scn|Sicilianu|Sicilian|IT
szl|Ślōnskŏ gŏdka|Silesian|PL
ss|SiSwati|Swati|SZ
ty|Reo Tahiti|Tahitian|PF
tzm|ⵜⴰⵎⴰⵣⵉⵖⵜ|Tamazight|MA
tet|Tetun|Tetum|TL
to|Lea faka-Tonga|Tongan|TO
lua|Tshiluba|Tshiluba|CD
tum|chiTumbuka|Tumbuka|MW
tyv|Тыва дыл|Tuvan|RU
udm|Удмурт|Udmurt|RU
ve|Tshivenḓa|Venda|ZA
war|Winaray|Waray|PH
yua|Maaya T'aan|Yucatec Maya|MX
zap|Diidxazá|Zapotec|MX
he|עברית (he)|Hebrew (alt)|IL
"""

        val ALL: List<TranslationLanguage> by lazy {
            val seen = HashSet<String>()
            RAW.trim().lines().mapNotNull { line ->
                val p = line.trim().split("|")
                if (p.size < 4) return@mapNotNull null
                if (p[0] == "he") return@mapNotNull null   // تکراری با iw
                if (!seen.add(p[0])) return@mapNotNull null
                TranslationLanguage(p[0], p[1], p[2], p[3])
            }
        }
    }
}
