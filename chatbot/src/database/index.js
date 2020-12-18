import Sequelize from 'sequelize'

import databaseConfig from '@/config/database'
import Answer from '@/models/Answer'
import Content from '@/models/Content'
import ContentHasAnswer from '@/models/ContentHasAnswer'
import ContentHasKeyword from '@/models/ContentHasKeyword'
import ContentHasLog from '@/models/ContentHasLog'
import ContentHasQuestion from '@/models/ContentHasQuestion'
import Keyword from '@/models/Keyword'
import Log from '@/models/Log'
import Message from '@/models/Message'
import MessageHasLog from '@/models/MessageHasLog'
import Monitoring from '@/models/Monitoring'
import MonitoringHasLog from '@/models/MonitoringHasLog'
import Profile from '@/models/Profile'
import Question from '@/models/Question'
import Sector from '@/models/Sector'
import Topic from '@/models/Topic'
import User from '@/models/User'

const connection = new Sequelize(databaseConfig)

connection
  .authenticate()
  .then(() => {
    console.info('Banco de dados conectado!')
  })
  .catch((error) => {
    console.error(`Não foi possível conectar no banco de dados!\n`
      + `Error name: ${error.name}\n`
      + `${error.parent}`)
  })

User.init(connection)
Profile.init(connection)
Sector.init(connection)
Content.init(connection)
Topic.init(connection)
Question.init(connection)
Answer.init(connection)
Keyword.init(connection)
Log.init(connection)
ContentHasQuestion.init(connection)
ContentHasAnswer.init(connection)
ContentHasKeyword.init(connection)
ContentHasLog.init(connection)
Message.init(connection)
MessageHasLog.init(connection)
Monitoring.init(connection)
MonitoringHasLog.init(connection)

// Associations
Content.belongsToMany(Question, { through: 'ContentHasQuestion', as: 'questions' })
Content.belongsToMany(Answer, { through: 'ContentHasAnswer', as: 'answers' })
Content.belongsToMany(Keyword, { through: 'ContentHasKeyword', as: 'keywords' })
Content.belongsToMany(Log, { through: 'ContentHasLog', as: 'contentLogs' })

Answer.belongsToMany(Content, { through: 'ContentHasAnswer', as: 'contents' })

Keyword.belongsToMany(Content, { through: 'ContentHasKeyword', as: 'contents' })

Question.belongsToMany(Content, { through: 'ContentHasQuestion', as: 'questions' })

Message.belongsToMany(Log, { through: 'MessageHasLog', as: 'messageLogs' })

Monitoring.belongsToMany(Log, { through: 'MonitoringHasLog', as: 'monitoringLogs' })

Log.belongsToMany(Content, { through: 'ContentHasLog', as: 'contentLogs' })
Log.belongsToMany(Message, { through: 'MessageHasLog', as: 'messageLogs' })
Log.belongsToMany(Monitoring, { through: 'MonitoringHasLog', as: 'monitoringLogs' })

export default connection
