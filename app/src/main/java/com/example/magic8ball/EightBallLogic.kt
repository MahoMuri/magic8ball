package com.example.magic8ball

import android.content.Context
import kotlin.random.Random

class EightBallLogic {
    companion object Statics {
        private var question_regex: Regex =
            Regex("^(?:is|are|does|do|will|was|has|what|which|whose|who|where|how|when|why)|(?:\\?|\\uff1f)\$", RegexOption.IGNORE_CASE)

        @JvmStatic
        fun generateResponse(ctx: Context, input: CharSequence): CharSequence {
            val responsesArray = ctx.resources.getStringArray(R.array.responsesArray)

            if (!question_regex.containsMatchIn(input))
                return responsesArray[Random.nextInt(10, 15)]
            return responsesArray[Random.nextInt(0, 20)]
        }
    }
}