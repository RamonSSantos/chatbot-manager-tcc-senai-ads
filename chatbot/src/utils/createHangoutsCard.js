export default function getHangoutsCard(title, topicDescription, subtitle, attachment) {
  const cardHeader = {
    title,
    subtitle: topicDescription,
  }
  const text = {
    textParagraph: {
      text: subtitle,
    },
  }
  const image = {
    image: {
      imageUrl: attachment,
      onClick: {
        openLink: {
          url: attachment,
        },
      },
    },
  }

  let infoSection
  if (attachment == null) {
    infoSection = {
      widgets: [text],
    }
  } else {
    infoSection = {
      widgets: [text, image],
    }
  }

  return {
    hangouts: {
      name: title,
      header: cardHeader,
      sections: [infoSection],
    },
  }
}
