# MIPT Skaičiuotuvas (Calculator)

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg?logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Design-Material%203-7B1FA2.svg)](https://m3.material.io/)
[![Min SDK](https://img.shields.io/badge/minSdk-26%20(Android%208.0)-brightgreen.svg)](https://developer.android.com/about/dashboards)
[![Target SDK](https://img.shields.io/badge/targetSdk-37-blue.svg)](https://developer.android.com/)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20UDF-orange.svg)]()

Moderni, greita ir patikima skaičiuotuvo programėlė, sukurta „Android“ operacinei sistemai naudojant moderniausias technologijas: **Kotlin**, **Jetpack Compose** deklaratyvią vartotojo sąsają bei **Material 3** dizaino sistemą. Programėlė atkartoja klasikinio stalinio/operacinės sistemos skaičiuotuvo patirtį su intuityviu valdymu, dinamiška operacijų istorija ir pilnu daugiakalbiškumo palaikymu.

---

## 📱 Funkcionalumas

Programėlė palaiko visas kasdieniam ir išplėstiniam naudojimui reikalingas aritmetines bei funkcines operacijas:

### 1. Pagrindinės binarinės operacijos
- **Sudėtis (`+`)**: dviejų ar daugiau skaičių sumavimas.
- **Atimtis (`−`)**: skaičių skirtumo apskaičiavimas.
- **Daugyba (`×`)**: skaičių sandaugos radimas.
- **Dalyba (`÷`)**: skaičių dalmuo su integruota apsauga nuo dalybos iš nulio (grąžinamas aiškus pranešimas, išvengiant programėlės lūžimo).

### 2. Išplėstinės unarinės ir specialios operacijos
- **Kvadratinė šaknis (`√x`)**: apskaičiuoja teigiamų skaičių šaknį (esant neigiamam skaičiui pateikiamas perspėjimas apie neteisingą įvestį).
- **Kėlimas kvadratu (`x²`)**: greitas skaičiaus pakėlimas antrąja pakopa.
- **Atvirkštinis skaičius / trupmena (`1/x`)**: apskaičiuoja vieneto ir įvesto skaičiaus santykį (su dalybos iš nulio apsauga).
- **Procentai (`%`)**: palaiko tiek paprastą konvertavimą į dešimtainę dalį ($x / 100$), tiek kontekstinį procentų skaičiavimą atliekant binarinį veiksmą (pvz., `200 + 10% = 220`).
- **Ženklo keitimas (`+/−`)**: skaičiaus invertavimas iš teigiamo į neigiamą ir atvirkščiai.
- **Dešimtainis kablelis / taškas (`.` / `,`)**: korektiškas realiųjų skaičių formavimas.

### 3. Įvesties ir atminties valdymas
- **C (Clear)**: visos esamos būsenos, operatorių ir išraiškos anuliavimas.
- **CE (Clear Entry)**: paskutinio įvesto skaičiaus išvalymas, išlaikant bendrą operacijos kontekstą.
- **⌫ (Backspace)**: paskutinio įvesto simbolio pašalinimas.
- **Atminties funkcijos**: `MC` (Clear), `MR` (Recall), `M+` (Add), `M−` (Subtract), `MS` (Store).
- **Apsauga nuo perpildymo**: ribojamas maksimalus įvesties ilgis (iki 15 skaitmenų).

### 4. Vykdymo laiko istorija (*Runtime History*)
- Kiekviena sėkmingai atlikta operacija automatiškai išsaugoma sesijos istorijoje (iki 50 paskutinių įrašų).
- Istorija atidaroma naudojant patogią, modernią Material 3 iškylančiąją kortelę (`ModalBottomSheet`).
- **Interaktyvumas**: paspaudus ant bet kurio ankstesnio skaičiavimo įrašo, jo rezultatas akimirksniu įkeliamas į skaičiuotuvo ekraną tolimesniems skaičiavimams.
- Galimybė vienu paspaudimu išvalyti visą istoriją.

### 5. Pilna lokalizacija (*Internationalization*)
- Programėlė automatiškai prisitaiko prie įrenginio kalbos nustatymų:
  - 🇱🇹 **Lietuvių kalba** (numatytoji `values/strings.xml`, naudojamas kablelis `,` kaip dešimtainis skyriklis).
  - 🇬🇧 **Anglų kalba** (`values-en/strings.xml`, naudojamas taškas `.` kaip dešimtainis skyriklis).
- Lokalizuoti visi mygtukų tekstai, klaidų pranešimai ir viršutinės juostos elementai.

---

## 🏗️ Architektūra

Projektas sukurtas vadovaujantis oficialiomis **Android Architecture Guidelines** rekomendacijomis ir švarios architektūros principais:

```
┌────────────────────────────────────────────────────────┐
│                   UI Layer (View)                      │
│     Jetpack Compose + Material 3 (CalculatorScreen)    │
└───────────────────────────▲────────────────────────────┘
                            │ StateFlow<CalculatorState>
                            │ User Actions (Events)
┌───────────────────────────▼────────────────────────────┐
│                      ViewModel                         │
│               CalculatorViewModel                      │
│   (Būsenos valdymas, UDF srautas, istorijos valdymas)  │
└───────────────────────────▲────────────────────────────┘
                            │ Kvietimai ir Result<Double>
┌───────────────────────────▼────────────────────────────┐
│                    Business Logic                      │
│                   CalculatorEngine                     │
│        (Gryna matematinė logika „from scratch“)        │
└────────────────────────────────────────────────────────┘
```

### Pagrindiniai architektūros akcentai:
- **MVVM (Model-View-ViewModel)**:
  - **View (UI)**: Deklaratyvūs „Compose“ komponentai (`CalculatorScreen`, `HistoryPanel`), kurie tik stebi būseną ir siunčia vartotojo veiksmus į ViewModel.
  - **ViewModel**: `CalculatorViewModel` laiko ir atnaujina nekintamą būseną (`CalculatorState`) per `StateFlow`, užtikrindamas vienkryptį duomenų srautą (*Unidirectional Data Flow – UDF*).
  - **Model / Logic**: `CalculatorEngine` – visiškai nepriklausomas modulis.
- **Matematinė logika sukurta „from scratch“**:
  - Visi skaičiavimai (sudėtis, atimtis, šaknis, kėlimas kvadratu, trupmenos, procentai ir dešimtainis formatavimas) realizuoti **savo rankomis be jokių trečiųjų šalių išorinių bibliotekų** (pvz., *exp4j*, *keval* ar kitų analizatorių).
  - Skaičiavimų rezultatai grąžinami naudojant saugų Kotlin `Result<Double>` tipą, leidžiantį elegantiškai apdoroti aritmetines išimtis (pvz., dalybą iš nulio ar neigiamo skaičiaus šaknį).

---

## 🚀 Paleidimo instrukcija

### Sistemos reikalavimai:
- **Android Studio**: Ladybug (arba naujesnė versija, pvz., Koala / Iguana / Jellyfish).
- **JDK**: Java 11 arba Java 17/21.
- **Android SDK**:
  - `minSdk`: **26** (Android 8.0 Oreo)
  - `targetSdk`: **37**
  - `compileSdk`: **37**

### Žingsniai paleidimui per Android Studio:
1. **Atsisiųskite arba klonuokite saugyklą**:
   ```bash
   git clone https://github.com/your-username/MIPT_Calculator.git
   ```
2. **Atidarykite projektą Android Studio**:
   - Pasirinkite **File -> Open...**
   - Nurodykite projekto šakninį aplanką (`MIPT_Calculator`).
3. **Sulaukite Gradle sinchronizacijos**:
   - Android Studio automatiškai atsiųs reikalingas priklausomybes ir sukonfigūruos projektą pagal `build.gradle.kts`.
4. **Pasirinkite įrenginį**:
   - Įrenginių sąraše pasirinkite emuliatorių arba prijungtą fizinį įrenginį su **Android 8.0 (API 26)** ar naujesne operacine sistema.
5. **Paleiskite programėlę**:
   - Paspauskite žalią paleidimo mygtuką **Run 'app'** (arba klavišų kombinaciją `Shift + F10` / `Ctrl + R`).

### Paleidimas per komandinę eilutę:
Projekto šakniniame aplanke galite sugeneruoti APK paketą arba tiesiogiai įdiegti į prijungtą įrenginį:

```bash
# Debug APK surinkimas (Windows PowerShell)
.\gradlew assembleDebug

# Tiesioginis diegimas į prijungtą įrenginį/emuliatorių
.\gradlew installDebug
```

---

## 📂 Projekto struktūra

Pagrindinių projekto failų ir aplankų medis:

```text
MIPT_Calculator/
├── app/
│   ├── build.gradle.kts                   # Modulio lygio Gradle konfigūracija ir priklausomybės
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml        # Programėlės manifestas ir konfigūracija
│       │   ├── java/com/mipt/calculator/
│       │   │   ├── MainActivity.kt        # Pagrindinis įėjimo taškas (ComponentActivity)
│       │   │   ├── CalculatorEngine.kt    # Matematinė logika „from scratch“ (grynas Kotlin)
│       │   │   ├── CalculatorViewModel.kt # MVVM būsenos valdymas (StateFlow, UDF)
│       │   │   └── ui/
│       │   │       ├── CalculatorScreen.kt# Pagrindinis Jetpack Compose skaičiuotuvo ekranas
│       │   │       ├── HistoryPanel.kt    # Istorijos langas (ModalBottomSheet)
│       │   │       └── theme/
│       │   │           ├── Color.kt       # Material 3 spalvų paletė
│       │   │           ├── Theme.kt       # Programėlės temos apibrėžimas
│       │   │           └── Type.kt        # Tipografija ir šriftai
│       │   └── res/
│       │       ├── values/
│       │       │   ├── strings.xml        # Lietuviški resursai (numatytoji lokalizacija)
│       │       │   ├── colors.xml         # Resursų spalvos
│       │       │   └── themes.xml         # XML temos
│       │       └── values-en/
│       │           └── strings.xml        # Angliški resursai (anglų k. lokalizacija)
│       └── test/                          # Vienetų testai (Unit tests)
├── build.gradle.kts                       # Projekto lygio Gradle skriptas
├── settings.gradle.kts                    # Projekto nustatymai ir repozitorijos
├── gradle.properties                      # Gradle JVM nustatymai
├── gradlew / gradlew.bat                  # Gradle Wrapper skriptai
├── CODE_REVIEW.md                         # Kodo peržiūros ataskaita ir refaktorinimo istorija
└── README.md                              # Pagrindinė projekto dokumentacija
```

---

## 📄 Licencija

Projektas sukurtas mokymosi ir demonstraciniais tikslais (MIPT). Kodas platinamas pagal atvirojo kodo standartus.
