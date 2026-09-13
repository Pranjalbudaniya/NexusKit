package com.nexuskit.app.feature.tools.science_education

import kotlin.math.*

data class ElementInfo(
    val number: Int,
    val symbol: String,
    val name: String,
    val mass: Double,
    val category: String, // Alkali Metal, Transition Metal, Noble Gas, etc.
    val group: Int,
    val period: Int,
    val electronConfig: String,
    val summary: String
)

data class PhysicalConstant(
    val name: String,
    val symbol: String,
    val value: String,
    val unit: String,
    val category: String
)

data class PlanetInfo(
    val name: String,
    val type: String,
    val massKg: String,
    val diameterKm: String,
    val gravityMps2: String,
    val orbitalPeriod: String,
    val moons: Int,
    val description: String
)

enum class ScienceTab {
    PERIODIC_TABLE, CONSTANTS, OHMS_LAW, PLANETS, TRIGONOMETRY, SCIENTIFIC_NOTATION, FORMULAS
}

object ScienceEngine {

    val periodicTable = listOf(
        ElementInfo(1, "H", "Hydrogen", 1.008, "Nonmetal", 1, 1, "1s¹", "Lightest element, fuel for stars."),
        ElementInfo(2, "He", "Helium", 4.0026, "Noble Gas", 18, 1, "1s²", "Colorless, odorless noble gas."),
        ElementInfo(3, "Li", "Lithium", 6.94, "Alkali Metal", 1, 2, "[He] 2s¹", "Soft metal used in rechargeable batteries."),
        ElementInfo(4, "Be", "Beryllium", 9.0122, "Alkaline Earth", 2, 2, "[He] 2s²", "Relatively rare metal in the universe."),
        ElementInfo(5, "B", "Boron", 10.81, "Metalloid", 13, 2, "[He] 2s² 2p¹", "Used in fiberglass and semiconductors."),
        ElementInfo(6, "C", "Carbon", 12.011, "Nonmetal", 14, 2, "[He] 2s² 2p²", "Basis of all organic life on Earth."),
        ElementInfo(7, "N", "Nitrogen", 14.007, "Nonmetal", 15, 2, "[He] 2s² 2p³", "Makes up 78% of Earth's atmosphere."),
        ElementInfo(8, "O", "Oxygen", 15.999, "Nonmetal", 16, 2, "[He] 2s² 2p⁴", "Essential for respiration and combustion."),
        ElementInfo(9, "F", "Fluorine", 18.998, "Halogen", 17, 2, "[He] 2s² 2p⁵", "Extremely reactive halogen."),
        ElementInfo(10, "Ne", "Neon", 20.180, "Noble Gas", 18, 2, "[He] 2s² 2p⁶", "Glows reddish-orange in high-voltage signs."),
        ElementInfo(11, "Na", "Sodium", 22.990, "Alkali Metal", 1, 3, "[Ne] 3s¹", "Essential electrolyte, component of salt."),
        ElementInfo(12, "Mg", "Magnesium", 24.305, "Alkaline Earth", 2, 3, "[Ne] 3s²", "Structural metal and vital biological cofactor."),
        ElementInfo(13, "Al", "Aluminum", 26.982, "Post-Transition", 13, 3, "[Ne] 3s² 3p¹", "Lightweight, corrosion-resistant metal."),
        ElementInfo(14, "Si", "Silicon", 28.085, "Metalloid", 14, 3, "[Ne] 3s² 3p²", "Backbone of modern microelectronics."),
        ElementInfo(15, "P", "Phosphorus", 30.974, "Nonmetal", 15, 3, "[Ne] 3s² 3p³", "Crucial component of DNA and ATP."),
        ElementInfo(16, "S", "Sulfur", 32.06, "Nonmetal", 16, 3, "[Ne] 3s² 3p⁴", "Yellow nonmetal, key in vulcanization."),
        ElementInfo(17, "Cl", "Chlorine", 35.45, "Halogen", 17, 3, "[Ne] 3s² 3p⁵", "Powerful disinfectant and bleach."),
        ElementInfo(18, "Ar", "Argon", 39.948, "Noble Gas", 18, 3, "[Ne] 3s² 3p⁶", "Inert gas used in welding and lighting."),
        ElementInfo(19, "K", "Potassium", 39.098, "Alkali Metal", 1, 4, "[Ar] 4s¹", "Vital mineral for cellular nerve signaling."),
        ElementInfo(20, "Ca", "Calcium", 40.078, "Alkaline Earth", 2, 4, "[Ar] 4s²", "Essential for bones, teeth, and muscles."),
        ElementInfo(26, "Fe", "Iron", 55.845, "Transition Metal", 8, 4, "[Ar] 3d⁶ 4s²", "Primary component of steel, carries oxygen in blood."),
        ElementInfo(28, "Ni", "Nickel", 58.693, "Transition Metal", 10, 4, "[Ar] 3d⁸ 4s²", "Resistant to corrosion, used in stainless steel."),
        ElementInfo(29, "Cu", "Copper", 63.546, "Transition Metal", 11, 4, "[Ar] 3d¹⁰ 4s¹", "High electrical and thermal conductivity."),
        ElementInfo(30, "Zn", "Zinc", 65.38, "Transition Metal", 12, 4, "[Ar] 3d¹⁰ 4s²", "Used to galvanize steel against rusting."),
        ElementInfo(47, "Ag", "Silver", 107.87, "Transition Metal", 11, 5, "[Kr] 4d¹⁰ 5s¹", "Highest electrical conductivity of any element."),
        ElementInfo(50, "Sn", "Tin", 118.71, "Post-Transition", 14, 5, "[Kr] 4d¹⁰ 5s² 5p²", "Silvery metal, component of bronze and solder."),
        ElementInfo(79, "Au", "Gold", 196.97, "Transition Metal", 11, 6, "[Xe] 4f¹⁴ 5d¹⁰ 6s¹", "Dense precious metal resistant to oxidation."),
        ElementInfo(80, "Hg", "Mercury", 200.59, "Transition Metal", 12, 6, "[Xe] 4f¹⁴ 5d¹⁰ 6s²", "Only metallic element liquid at standard conditions."),
        ElementInfo(82, "Pb", "Lead", 207.2, "Post-Transition", 14, 6, "[Xe] 4f¹⁴ 5d¹⁰ 6s² 6p²", "Heavy, dense metal used in radiation shielding."),
        ElementInfo(92, "U", "Uranium", 238.03, "Actinide", 3, 7, "[Rn] 5f³ 6d¹ 7s²", "Dense radioactive element used in nuclear reactors.")
    )

    val constants = listOf(
        PhysicalConstant("Speed of Light in Vacuum", "c", "299,792,458", "m/s", "Electromagnetism"),
        PhysicalConstant("Planck Constant", "h", "6.62607015 × 10⁻³⁴", "J·s", "Quantum"),
        PhysicalConstant("Reduced Planck Constant", "ℏ", "1.054571817 × 10⁻³⁴", "J·s", "Quantum"),
        PhysicalConstant("Gravitational Constant", "G", "6.67430 × 10⁻¹¹", "m³·kg⁻¹·s⁻²", "Gravity"),
        PhysicalConstant("Elementary Charge", "e", "1.602176634 × 10⁻¹⁹", "C", "Electromagnetism"),
        PhysicalConstant("Boltzmann Constant", "k_B", "1.380649 × 10⁻²³", "J/K", "Thermodynamics"),
        PhysicalConstant("Avogadro Constant", "N_A", "6.02214076 × 10²³", "mol⁻¹", "Chemistry"),
        PhysicalConstant("Universal Gas Constant", "R", "8.314462618", "J·mol⁻¹·K⁻¹", "Thermodynamics"),
        PhysicalConstant("Standard Gravity (Earth)", "g₀", "9.80665", "m/s²", "Gravity"),
        PhysicalConstant("Electron Mass", "m_e", "9.1093837015 × 10⁻³¹", "kg", "Quantum"),
        PhysicalConstant("Proton Mass", "m_p", "1.67262192369 × 10⁻²⁷", "kg", "Quantum"),
        PhysicalConstant("Vacuum Permittivity", "ε₀", "8.8541878128 × 10⁻¹²", "F/m", "Electromagnetism"),
        PhysicalConstant("Vacuum Permeability", "μ₀", "1.25663706212 × 10⁻⁶", "N/A²", "Electromagnetism"),
        PhysicalConstant("Stefan-Boltzmann Constant", "σ", "5.670374419 × 10⁻⁸", "W·m⁻²·K⁻⁴", "Thermodynamics")
    )

    val planets = listOf(
        PlanetInfo("Mercury", "Terrestrial", "3.30 × 10²³", "4,879", "3.7", "88 days", 0, "Smallest and closest planet to the Sun with extreme temperature swings."),
        PlanetInfo("Venus", "Terrestrial", "4.87 × 10²⁴", "12,104", "8.87", "225 days", 0, "Hottest planet in the solar system due to a dense runaway greenhouse atmosphere."),
        PlanetInfo("Earth", "Terrestrial", "5.97 × 10²⁴", "12,742", "9.81", "365.25 days", 1, "Only known celestial body harboring active biological life and liquid oceans."),
        PlanetInfo("Mars", "Terrestrial", "6.42 × 10²³", "6,779", "3.71", "687 days", 2, "The Red Planet, home to Olympus Mons and Valles Marineris canyon system."),
        PlanetInfo("Jupiter", "Gas Giant", "1.90 × 10²⁷", "139,820", "24.79", "11.86 years", 95, "Largest planet in the solar system, with iconic Great Red Spot storm."),
        PlanetInfo("Saturn", "Gas Giant", "5.68 × 10²⁶", "116,460", "10.44", "29.45 years", 146, "Distinguished by an extensive and brilliant planetary ring system."),
        PlanetInfo("Uranus", "Ice Giant", "8.68 × 10²⁵", "50,724", "8.69", "84.0 years", 28, "Ice giant tilted nearly 98 degrees on its rotational axis."),
        PlanetInfo("Neptune", "Ice Giant", "1.02 × 10²⁶", "49,244", "11.15", "164.8 years", 16, "Most distant major planet, featuring high-speed supersonic winds.")
    )

    // Ohm's law calculations
    fun calculateOhmsLaw(voltage: Double?, current: Double?, resistance: Double?, power: Double?): Map<String, Double> {
        val results = mutableMapOf<String, Double>()
        when {
            voltage != null && current != null -> {
                val v = voltage
                val i = current
                results["V"] = v
                results["I"] = i
                results["R"] = if (i != 0.0) v / i else 0.0
                results["P"] = v * i
            }
            voltage != null && resistance != null -> {
                val v = voltage
                val r = resistance
                results["V"] = v
                results["R"] = r
                results["I"] = if (r != 0.0) v / r else 0.0
                results["P"] = if (r != 0.0) (v * v) / r else 0.0
            }
            current != null && resistance != null -> {
                val i = current
                val r = resistance
                results["I"] = i
                results["R"] = r
                results["V"] = i * r
                results["P"] = i * i * r
            }
            voltage != null && power != null -> {
                val v = voltage
                val p = power
                results["V"] = v
                results["P"] = p
                results["I"] = if (v != 0.0) p / v else 0.0
                results["R"] = if (p != 0.0) (v * v) / p else 0.0
            }
            current != null && power != null -> {
                val i = current
                val p = power
                results["I"] = i
                results["P"] = p
                results["V"] = if (i != 0.0) p / i else 0.0
                results["R"] = if (i != 0.0) p / (i * i) else 0.0
            }
            resistance != null && power != null -> {
                val r = resistance
                val p = power
                val v = sqrt(p * r)
                val i = if (r != 0.0) sqrt(p / r) else 0.0
                results["R"] = r
                results["P"] = p
                results["V"] = v
                results["I"] = i
            }
        }
        return results
    }

    // Trigonometry calculator
    fun calculateTrigonometry(angle: Double, isDegrees: Boolean): Map<String, Double> {
        val rad = if (isDegrees) Math.toRadians(angle) else angle
        return mapOf(
            "sin" to sin(rad),
            "cos" to cos(rad),
            "tan" to if (abs(cos(rad)) > 1e-10) tan(rad) else Double.NaN,
            "cot" to if (abs(sin(rad)) > 1e-10) 1.0 / tan(rad) else Double.NaN,
            "sec" to if (abs(cos(rad)) > 1e-10) 1.0 / cos(rad) else Double.NaN,
            "csc" to if (abs(sin(rad)) > 1e-10) 1.0 / sin(rad) else Double.NaN
        )
    }

    // Scientific notation converter
    fun toScientificNotation(numberStr: String): String {
        val num = numberStr.trim().toDoubleOrNull() ?: return "Invalid number"
        if (num == 0.0) return "0.0 × 10⁰"
        val exponent = floor(log10(abs(num))).toInt()
        val mantissa = num / 10.0.pow(exponent.toDouble())
        return String.format(java.util.Locale.US, "%.4f × 10^%d", mantissa, exponent)
    }

    fun fromScientificNotation(mantissaStr: String, exponentStr: String): String {
        val m = mantissaStr.trim().toDoubleOrNull() ?: return "Invalid"
        val e = exponentStr.trim().toIntOrNull() ?: return "Invalid"
        val res = m * 10.0.pow(e.toDouble())
        return String.format(java.util.Locale.US, "%.8f", res).trimEnd('0').trimEnd('.')
    }
}
