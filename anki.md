# AnkiDroid integration

### How to add notes to AnkiDroid?

1. Make sure AnkiDroid is installed and "Enable AnkiDroid API" is enabled in AnkiDroid's settings. (enabled by default)
2. Enable AnkiDroid integration in Kata's settings and allow AnkiDroid permissions in the pop-up window.
3. Select a deck in AnkiDroid to add notes. (if not, a new deck named Kata will be created to add notes)
4. When you see a word you wish to add to AnkiDroid while using Kata, click the [+] to the right of the word.(If the word has been added to AnkiDroid, [+] will appear translucent)

### What does Kata use to determine if a word is in AnkiDroid?

Kata will not add a word again as long as `Expression+Furigana` and the `note type` are not changed in the note added by Kata.

**More details:**

A note added to AnkiDroid will contain the following 5 fields:

* Expression+Furigana
* Expression
* Furigana
* Meaning
* ContextStr

And each note will have a note type named `im.dacer.kata-v***` (e.g. im.dacer.kata-v3.0)

So in other words, as long as you don't modify the `Expression+Furigana` and `note type` of the note, you can change the tags, meaning and even furigana of a note or move a note to another deck, Kata will still can recognize that it is a word that has been added. 

### What if the note type is changed in a future update?

Kata will also try to find notes that were added in the old version.

(Due to some technical problems, im.dacer.kata-v2.0 and the previous note type will not be searched.)

### Can I customize the note style?

Of course, you can modify the card style in the note type named `im.dacer.kata-v***` via AnkiDroid or Anki Desktop.


For details, please refer to: [https://docs.ankidroid.org/manual.html#customizingCardLayout](https://docs.ankidroid.org/manual.html#customizingCardLayout)

### Please give Kata a five-star rating in Google Play if you think it's helpful to you.

If you have any questions, please feel free to contact the author via dacerfeedback+kata@gmail.com.