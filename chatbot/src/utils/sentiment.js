import sentiment from 'sentiment-ptbr'

export default function sentimentAnalysis(phrase) {
  return sentiment(phrase)
}
