# Kodo peržiūra („Code Review“) – MIPT Calculator

Šiame dokumente pateikiama projekto kodo peržiūros ataskaita, identifikuotos problemos, realizuoti aukščiausio prioriteto patobulinimai bei rekomendacijos ateities etapams.

---

## 1. Santrauka ir prioritetai

Atlikus pirminio kodo analizę (Jetpack Compose, MVVM architektūra, tematinis apipavidalinimas), buvo išskirti šie pagrindiniai aspektai:

1. **Aukštas prioritetas (Kritinis UX)**: Istorijos skydelis atsidarydavo kaip atskiras pilnas ekranas (`slideInHorizontally`) be galimybės vartotojui lengvai grįžti ar užverti langą.
2. **Aukštas prioritetas (Dizaino / UI atitikimas)**: Mygtukų spalvų schema rėmėsi melsvais atspalviais veiksmo mygtukams (`+`, `-`, `×`, `÷`), o „pop-out“ piktograma buvo nutolusi nuo režimo antraštės.
3. **Vidutinis prioritetas (Architektūra ir tikslumas)**: Būsenos valdymas istorijai (`onToggleHistory`), slankiojo kablelio operacijų tikslumas (`Double` vs `BigDecimal`).
4. **Žemas prioritetas (Kodo švara)**: Didelis `ButtonGrid` argumentų kiekis, modelių atskyrimas į atskirus paketus, vienetų testų aprėptis.

---

## 2. Realizuoti aukščiausio prioriteto pakeitimai

Tiesiogiai projekto kode buvo realizuoti du svarbiausi užduotyje nurodyti pakeitimai:

### 1 pakeitimas: Istorijos lango UX pertvarkymas į `ModalBottomSheet`
- **Problema**: Istorijos ekranas uždengdavo visą skaičiuotuvo vaizdą per visą plotį ir neturėjo aiškaus uždarymo / grįžimo mygtuko, apsunkindamas vartotojo patirtį.
- **Sprendimas**:
  - `HistoryPanel.kt` komponentas pertvarkytas naudojant Material 3 `ModalBottomSheet` komponentą su `BottomSheetDefaults.DragHandle()`.
  - Nustatytas optimalus iškylančio skydelio aukštis (`fillMaxHeight(0.62f)`), leidžiantis fone matyti skaičiuotuvo kontekstą.
  - Vartotojas gali bet kada užverti istoriją trimis natūraliais būdais:
    1. Nubraukdamas kortelę žemyn (drag gesture).
    2. Paliesdamas patamsintą foną už kortelės ribų (`onDismiss`).
    3. Pasirinkdamas konkretų istorijos įrašą, kuris automatiškai įkelia rezultatą į skaičiuotuvo ekraną ir uždaro kortelę.
  - Pridėtas istorijos išvalymo mygtukas kortelės viršuje šalia antraštės.
- **Pakeisti failai**:
  - `app/src/main/java/com/mipt/calculator/ui/HistoryPanel.kt`
  - `app/src/main/java/com/mipt/calculator/ui/CalculatorScreen.kt`

### 2 pakeitimas: UI mygtukų spalvų tikslumas ir „pop-out“ mygtuko pozicija
- **Problema**:
  - Operacijų mygtukai (`+`, `-`, `×`, `÷`) turėjo ryškų melsvą atspalvį (`#DDEDFF`), neatitinkantį klasikinio šviesaus „Windows“ skaičiuotuvo pavyzdžio.
  - „Pop-out“ mygtukas viršutinėje juostoje buvo dešinėje pusėje, o ne šalia režimo pavadinimo „Standard“.
- **Sprendimas**:
  - Operacijų mygtukams (`ButtonType.OPERATOR`) priskirta neutrali šviesiai pilkšva spalva (`#E6E6E6`), identiška funkcijų mygtukams (`CE`, `C`, `%`, ir kt.), o aktyviam operatoriui – tamsiai pilka (`#4A4A4A`) su baltu tekstu.
  - Pagrindinis lygybės mygtukas (`=`) išlaikytas akcentine mėlyna spalva (`#0067C0`), o skaičių mygtukai – švariai balti (`#FFFFFF`).
  - Viršutinėje juostoje (`CalculatorTopBar`) „pop-out“ piktograma perkelta tiesiai šalia užrašo „Standard“, o istorijos mygtukas (`History`) atitrauktas į dešinį kraštą (`Spacer(Modifier.weight(1f))`), tiksliai atkartojant užduoties šabloną.
- **Pakeisti failai**:
  - `app/src/main/java/com/mipt/calculator/ui/theme/Color.kt`
  - `app/src/main/java/com/mipt/calculator/ui/CalculatorScreen.kt`

---

## 3. Detali kodo peržiūros analizė pagal komponentus

### 3.1. `CalculatorEngine.kt` (Skaičiavimo modulis)
* **Privalumai**:
  - Sukurtas savarankiškai („from scratch“) nenaudojant trečiųjų šalių bibliotekų.
  - Tvarkingai apdorojama dalyba iš nulio (`Result.failure`).
  - Realizuotos visos reikalaujamos operacijos: šaknis, kvadratas, trupmena ($1/x$), procentai, ženklo keitimas.
* **Pastebėjimai ir tobulinimo rekomendacijos**:
  - `Double` tipo aritmetika gali sukelti tipiškas slankiojo kablelio apvalinimo paklaidas (pvz., `0.1 + 0.2`). Tikslumui ateityje rekomenduojama naudoti `BigDecimal`.
  - Rezultatų formatavimas (`formatResult`): verta apsvarstyti lokalizuotą tūkstančių skirtukų ir dešimtainių kablelių atvaizdavimą.

### 3.2. `CalculatorViewModel.kt` (Būsena ir verslo logika)
* **Privalumai**:
  - Naudojamas `StateFlow` ir neredaguojamas `CalculatorState` duomenų modelis.
  - Istorijos įrašymas vykdomas korektiškai po kiekvieno skaičiavimo (`MAX_HISTORY = 50`).
  - Atminties funkcijos (`MC`, `MR`, `M+`, `M-`, `MS`) pilnai realizuotos.
* **Pastebėjimai**:
  - `onToggleHistory()` perjungia loginę reikšmę. Siekiant išvengti sinchronizacijos klaidų su `ModalBottomSheet`, galima atskirti `showHistory()` ir `hideHistory()`.
  - Įvesties simbolių ilgio apribojimas (`MAX_INPUT_LENGTH = 15`) veikia teisingai filtruodamas kablelius ir minuso ženklus.

### 3.3. `CalculatorScreen.kt` ir `HistoryPanel.kt` (Vartotojo sąsaja)
* **Privalumai**:
  - Švari deklaratyvi Jetpack Compose struktūra.
  - Tekstai iškelti į resursų failus (`strings.xml`, `values-en/strings.xml`), užtikrinant lengvą lokalizaciją.
  - Mygtukų tinklelis pritaikytas proporcingam skydelio išdėstymui (`aspectRatio(1.1f)`).
* **Pastebėjimai**:
  - `ButtonGrid` priima 25 atskirus atgalinio iškvietimo (callback) parametrus. Kodo palaikymui patogiau būtų apjungti juos į įvykių klasę (pvz., `sealed class CalculatorAction` arba `data class ButtonCallbacks`).

---

## 4. Modifikuotų failų suvestinė

| Failas | Atlikto pakeitimo esmė |
|---|---|
| `app/src/main/java/com/mipt/calculator/ui/HistoryPanel.kt` | Realizuotas `ModalBottomSheet` su gestų palaikymu, `onDismiss`, trynimo mygtuku ir proporcingu aukščiu. |
| `app/src/main/java/com/mipt/calculator/ui/CalculatorScreen.kt` | Atnaujinta `CalculatorTopBar` kompozicija („pop-out“ perkeltas prie „Standard“); neutralios pilkos spalvos operatorių mygtukams. |
| `app/src/main/java/com/mipt/calculator/ui/theme/Color.kt` | Atnaujintos spalvų konstantos: `CalcSecondary` ir `CalcSecondaryContainer` neutraliems atspalviams. |
| `CODE_REVIEW.md` | Sukurta išsami atliktos kodo peržiūros ataskaita projekto šakniniame kataloge. |

---

## 5. Išvada

Projektas atitinka nurodytus reikalavimus: skaičiavimo modulis nepriklausomas, palaikoma atminties ir istorijos sistema, mygtukų tekstai lokalizuoti. Atlikti UX ir UI pakeitimai pašalina kritinį navigacijos trūkumą istorijos lange bei užtikrina vizualinį atitikimą klasikiniam „Windows“ skaičiuotuvo etalonui.
