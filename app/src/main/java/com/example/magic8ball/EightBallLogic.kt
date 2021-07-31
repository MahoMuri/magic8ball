package com.example.magic8ball

import android.content.Context
import kotlin.random.Random

class EightBallLogic {
    companion object Statics {
        private var question_regex: Regex =
            Regex("^(?:is|are|does|do|will|was|has|what|which|whose|who|where|how|when|why)|(?:\\?|\\uff1f)\$", RegexOption.IGNORE_CASE)

        @JvmStatic
        fun generateResponse(ctx: Context, input: CharSequence): CharSequence {
            if (!question_regex.containsMatchIn(input))
                return internalGetString(ctx, 10, 15)
            return internalGetString(ctx, 0, 20)
        }

        @JvmStatic
        private fun internalGetString(ctx: Context, start: Int, end: Int): String {
            val responsesArray = ctx.resources.getStringArray(R.array.responsesArray)
            return responsesArray[Random.nextInt(start, end)]
        }
    }
}