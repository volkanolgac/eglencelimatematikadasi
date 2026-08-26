package com.example.carpimadasi.logic

import com.example.carpimadasi.model.OperationType
import com.example.carpimadasi.model.Question
import com.example.carpimadasi.model.QuestionFormat
import com.example.carpimadasi.model.WrongRecord
import kotlin.random.Random

object QuestionGenerator {

    private val MULTIPLICATION_VISUALS = listOf(
        { a: Int, b: Int -> "$a kutuda her birinde $b elma var. Toplam kaç elma?" },
        { a: Int, b: Int -> "$a sepetin her birinde $b muz var. Toplam kaç muz?" },
        { a: Int, b: Int -> "$a kavanozda her birinde $b bisküvi var. Toplam kaç bisküvi?" },
        { a: Int, b: Int -> "$a sırada her birinde $b öğrenci var. Toplam kaç öğrenci?" },
        { a: Int, b: Int -> "$a çantada her birinde $b kalem var. Toplam kaç kalem?" }
    )

    private val ADDITION_VISUALS = listOf(
        { a: Int, b: Int -> "$a kırmızı balona $b mavi balon eklendi. Toplam kaç balon oldu?" },
        { a: Int, b: Int -> "$a kuşun yanına $b kuş daha kondu. Toplam kaç kuş oldu?" },
        { a: Int, b: Int -> "$a elmaya $b elma daha katıldı. Toplam kaç elma var?" },
        { a: Int, b: Int -> "Bahçede $a sarı çiçek, $b beyaz çiçek açtı. Toplam kaç çiçek var?" },
        { a: Int, b: Int -> "$a oyuncak arabana $b araba daha hediye geldi. Toplam kaç araban oldu?" }
    )

    private val SUBTRACTION_VISUALS = listOf(
        { c: Int, a: Int -> "Tabaktaki $c kurabiyenin $a tanesi yendi. Kaç kurabiye kaldı?" },
        { c: Int, a: Int -> "Ağaçtaki $c elmanın $a tanesi sepete toplandı. Ağaçta kaç elma kaldı?" },
        { c: Int, a: Int -> "$c balonun $a tanesi gökyüzüne uçtu. Elimizde kaç balon kaldı?" },
        { c: Int, a: Int -> "Kütüphanedeki $c kitabın $a tanesi okundu. Okunacak kaç kitap kaldı?" },
        { c: Int, a: Int -> "Daldaki $c kuştan $a tanesi uçtu. Dalda kaç kuş kaldı?" }
    )

    private val DIVISION_VISUALS = listOf(
        { c: Int, a: Int -> "$c fındık $a sincap arasında eşit paylaştırıldı. Her birine kaç fındık düşer?" },
        { c: Int, a: Int -> "$c şeker $a çocuğa eşit dağıtıldı. Her çocuğa kaç şeker düşer?" },
        { c: Int, a: Int -> "$c elma $a sepete eşit paylaştırıldı. Her sepette kaç elma olur?" },
        { c: Int, a: Int -> "$c kalem $a kutuya eşit bölüştürüldü. Her kutuda kaç kalem var?" },
        { c: Int, a: Int -> "$c dilim pizza $a tabağa eşit paylaştırıldı. Her tabakta kaç dilim var?" }
    )

    private fun makeOptions(answer: Int, table: Int): List<Int> {
        val opts = mutableSetOf(answer)
        val deltas = listOf(-3, -2, -1, 1, 2, 3, 4, -4, table, -table, 5, -5)

        var guard = 0
        while (opts.size < 4 && guard < 60) {
            guard++
            val delta = deltas.random()
            val cand = answer + delta
            if (cand > 0 && cand != answer) {
                opts.add(cand)
            }
        }

        var offset = 1
        while (opts.size < 4) {
            if (answer - offset > 0) {
                opts.add(answer - offset)
            }
            if (opts.size < 4) {
                opts.add(answer + offset)
            }
            offset++
        }

        return opts.toList().shuffled()
    }

    fun makeQuestion(
        operation: OperationType = OperationType.MULTIPLICATION,
        table: Int,
        level: Int,
        wrongs: List<WrongRecord>,
        questionIndex: Int
    ): Question {
        // Spaced repetition weighting
        data class PoolItem(val a: Int, val b: Int, val weight: Int)
        val pool = mutableListOf<PoolItem>()

        for (m in 1..10) {
            val a = table
            val b = m
            val key = wrongKey(operation, a, b)
            val rec = wrongs.find { it.key == key }
            val weight = if (rec != null) 1 + rec.count * 2 else 1
            pool.add(PoolItem(a, b, weight))
        }

        val totalWeight = pool.sumOf { it.weight }
        var r = Random.nextInt(totalWeight)
        var pick = pool.first()
        for (item in pool) {
            r -= item.weight
            if (r < 0) {
                pick = item
                break
            }
        }

        val a = pick.a
        val b = pick.b

        val formats = mutableListOf(QuestionFormat.STANDARD, QuestionFormat.REVERSED)
        if (level >= 2) {
            formats.add(QuestionFormat.MISSING_SECOND)
            formats.add(QuestionFormat.MISSING_FIRST)
        }
        if (level >= 3) {
            formats.add(QuestionFormat.VISUAL)
        }

        val format = formats[questionIndex % formats.size]

        var prompt: String
        var subPrompt: String? = null
        var expectedAnswer: Int

        when (operation) {
            OperationType.MULTIPLICATION -> {
                val product = a * b
                when (format) {
                    QuestionFormat.STANDARD -> {
                        prompt = "$a × $b = ?"
                        expectedAnswer = product
                    }
                    QuestionFormat.REVERSED -> {
                        prompt = "$b × $a = ?"
                        expectedAnswer = product
                    }
                    QuestionFormat.MISSING_SECOND -> {
                        prompt = "$a × ? = $product"
                        expectedAnswer = b
                    }
                    QuestionFormat.MISSING_FIRST -> {
                        prompt = "? × $b = $product"
                        expectedAnswer = a
                    }
                    QuestionFormat.VISUAL -> {
                        val tpl = MULTIPLICATION_VISUALS.random()
                        prompt = tpl(a, b)
                        subPrompt = "$a × $b = ?"
                        expectedAnswer = product
                    }
                }
            }

            OperationType.ADDITION -> {
                val sum = a + b
                when (format) {
                    QuestionFormat.STANDARD -> {
                        prompt = "$a + $b = ?"
                        expectedAnswer = sum
                    }
                    QuestionFormat.REVERSED -> {
                        prompt = "$b + $a = ?"
                        expectedAnswer = sum
                    }
                    QuestionFormat.MISSING_SECOND -> {
                        prompt = "$a + ? = $sum"
                        expectedAnswer = b
                    }
                    QuestionFormat.MISSING_FIRST -> {
                        prompt = "? + $b = $sum"
                        expectedAnswer = a
                    }
                    QuestionFormat.VISUAL -> {
                        val tpl = ADDITION_VISUALS.random()
                        prompt = tpl(a, b)
                        subPrompt = "$a + $b = ?"
                        expectedAnswer = sum
                    }
                }
            }

            OperationType.SUBTRACTION -> {
                val total = a + b
                when (format) {
                    QuestionFormat.STANDARD -> {
                        prompt = "$total - $a = ?"
                        expectedAnswer = b
                    }
                    QuestionFormat.REVERSED -> {
                        prompt = "$total - $b = ?"
                        expectedAnswer = a
                    }
                    QuestionFormat.MISSING_SECOND -> {
                        prompt = "$total - ? = $b"
                        expectedAnswer = a
                    }
                    QuestionFormat.MISSING_FIRST -> {
                        prompt = "? - $a = $b"
                        expectedAnswer = total
                    }
                    QuestionFormat.VISUAL -> {
                        val tpl = SUBTRACTION_VISUALS.random()
                        prompt = tpl(total, a)
                        subPrompt = "$total - $a = ?"
                        expectedAnswer = b
                    }
                }
            }

            OperationType.DIVISION -> {
                val dividend = a * b
                when (format) {
                    QuestionFormat.STANDARD -> {
                        prompt = "$dividend ÷ $a = ?"
                        expectedAnswer = b
                    }
                    QuestionFormat.REVERSED -> {
                        prompt = "$dividend ÷ $b = ?"
                        expectedAnswer = a
                    }
                    QuestionFormat.MISSING_SECOND -> {
                        prompt = "$dividend ÷ ? = $b"
                        expectedAnswer = a
                    }
                    QuestionFormat.MISSING_FIRST -> {
                        prompt = "? ÷ $a = $b"
                        expectedAnswer = dividend
                    }
                    QuestionFormat.VISUAL -> {
                        val tpl = DIVISION_VISUALS.random()
                        prompt = tpl(dividend, a)
                        subPrompt = "$dividend ÷ $a = ?"
                        expectedAnswer = b
                    }
                }
            }
        }

        val options = makeOptions(expectedAnswer, table)

        return Question(
            a = a,
            b = b,
            answer = expectedAnswer,
            format = format,
            options = options,
            prompt = prompt,
            subPrompt = subPrompt,
            operation = operation
        )
    }

    fun wrongKey(op: OperationType, a: Int, b: Int): String = "${op.id}_${a}_${b}"
}

