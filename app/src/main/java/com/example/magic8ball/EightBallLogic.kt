package com.example.magic8ball

import android.content.Context
import kotlin.random.Random

class EightBallLogic {
    companion object Statics {
        private var question_regex: Regex =
            Regex("^(?:is|are|does|do|will|was|has|what|which|whose|who|where|how|when|why)|(?:\\?|\\uff1f)\$", RegexOption.IGNORE_CASE);

        @JvmStatic
        fun generateResponse(ctx: Context, input: CharSequence): CharSequence {
            if (!question_regex.containsMatchIn(input))
                return internalGetString(ctx, 10, 15);
            return internalGetString(ctx, 0, 20);
        }

        @JvmStatic
        fun internalGetString(ctx: Context, start: Int, end: Int): String {
            return when (val i = Random.nextInt(start, end)) {
                0 -> ctx.getString(R.string.eightball_response_0);
                1 -> ctx.getString(R.string.eightball_response_1);
                2 -> ctx.getString(R.string.eightball_response_2);
                3 -> ctx.getString(R.string.eightball_response_3);
                4 -> ctx.getString(R.string.eightball_response_4);
                5 -> ctx.getString(R.string.eightball_response_5);
                6 -> ctx.getString(R.string.eightball_response_6);
                7 -> ctx.getString(R.string.eightball_response_7);
                8 -> ctx.getString(R.string.eightball_response_8);
                9 -> ctx.getString(R.string.eightball_response_9);
                10 -> ctx.getString(R.string.eightball_response_10);
                11 -> ctx.getString(R.string.eightball_response_11);
                12 -> ctx.getString(R.string.eightball_response_12);
                13 -> ctx.getString(R.string.eightball_response_13);
                14 -> ctx.getString(R.string.eightball_response_14);
                15 -> ctx.getString(R.string.eightball_response_15);
                16 -> ctx.getString(R.string.eightball_response_16);
                17 -> ctx.getString(R.string.eightball_response_17);
                18 -> ctx.getString(R.string.eightball_response_18);
                19 -> ctx.getString(R.string.eightball_response_19);

                // Should be an impossible condition
                else -> "";

            }
        }
    }
}