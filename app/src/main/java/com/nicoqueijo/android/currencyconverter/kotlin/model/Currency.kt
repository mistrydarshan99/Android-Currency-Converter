package com.nicoqueijo.android.currencyconverter.kotlin.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils
import java.lang.StringBuilder
import java.math.BigDecimal

@Entity(tableName = "table_currency")
data class Currency(@PrimaryKey
                    @ColumnInfo(name = "column_currencyCode")
                    val currencyCode: String,
                    @ColumnInfo(name = "column_exchangeRate")
                    val exchangeRate: Double) {

    @Ignore
    var conversionValue = BigDecimal(0.0)

    @Ignore
    var conversion = Conversion(BigDecimal(0.0))

    @ColumnInfo(name = "column_isSelected")
    var isSelected = false

    @ColumnInfo(name = "column_order")
    var order = -1

    @Ignore
    var isFocused = false

    val trimmedCurrencyCode
        get() = currencyCode.substring(CURRENCY_CODE_STARTING_INDEX)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Currency
        if (currencyCode != other.currencyCode) return false
        return true
    }

    override fun hashCode() = currencyCode.hashCode()

    /*override fun toString() = "ordr: $order " +
            "slct: ${isSelected.toString().capitalize().take(1)} " +
            "fcsd: ${isFocused.toString().capitalize().take(1)} " +
            currencyCode*/

     /* Structure is for debugging purposes.
        Example: 4 S* F* USD_EUR
                 | |  |    |
             Order |  |    |
            Selected? |    |
                 Focused?  |
                     Currency code

         *blank if not selected/focused      */
    override fun toString(): String {
        val string = StringBuilder()
        string.append(order)
        string.append("  ")
        if (isSelected) {
            string.append("S")
        } else {
            string.append(" ")
        }
        string.append("  ")
        if (isFocused) {
            string.append("F")
        } else {
            string.append(" ")
        }
        string.append("  ")
        string.append(currencyCode)
        return string.toString()
    }

    companion object {
        const val CURRENCY_CODE_STARTING_INDEX = 4
    }

    class Conversion(conversionValue: BigDecimal) {

        // The raw underlying conversion result
        var conversionValue: BigDecimal = conversionValue
            set(value) {
                field = Utils.roundBigDecimal(value)
                conversionText = conversionValue.toString()
            }

        // The conversion result rounded and formatted
        lateinit var conversionText: String

        // The hint displayed when it is empty
        lateinit var conversionHint: String
    }

}