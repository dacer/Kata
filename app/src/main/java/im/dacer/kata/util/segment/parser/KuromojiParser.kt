package im.dacer.kata.util.segment.parser

import com.atilika.kuromoji.TokenizerBase
import com.atilika.kuromoji.ipadic.Token
import com.atilika.kuromoji.ipadic.Tokenizer
import im.dacer.kata.util.segment.SimpleParser

/**
 * Created by Dacer on 09/01/2018.
 */

class KuromojiParser : SimpleParser() {

    private val tokenizer = Tokenizer.Builder().mode(TokenizerBase.Mode.NORMAL).build()

    override fun parseSync(text: String): List<Token> {
        return tokenizer.tokenize(text)
    }
}
