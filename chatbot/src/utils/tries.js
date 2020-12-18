import { PorterStemmer, Trie } from 'natural'
import ptbr from 'stopword'

export default function tries(listOfWords, phrase) {
  let words

  if (typeof phrase === 'string') {
    const tokenizer = phrase.split(' ')
    words = ptbr.removeStopwords(tokenizer, ptbr.sv)
  } else if (typeof phrase === 'object') {
    words = phrase
  }

  const trie = new Trie(false)
  trie.addStrings(listOfWords.map((word) => PorterStemmer.stem(word)))

  return words.filter((word) => trie.contains(PorterStemmer.stem(word)))
}
