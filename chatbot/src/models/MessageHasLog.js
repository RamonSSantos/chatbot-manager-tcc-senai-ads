import { Model, DataTypes } from 'sequelize'

class MessageHasLog extends Model {
  static init(connection) {
    super.init({
      message_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
      },
      log_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
      },
    }, {
      sequelize: connection,
      tableName: 'message_has_log',
    })
  }
}

MessageHasLog.associations = (models) => {
  MessageHasLog.belongsTo(models.Message, { foreignKey: 'message_id' })
  MessageHasLog.belongsTo(models.Log, { foreignKey: 'log_id' })
}

export default MessageHasLog
