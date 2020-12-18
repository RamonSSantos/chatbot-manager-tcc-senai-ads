import { Model, DataTypes } from 'sequelize'

class ContentHasLog extends Model {
  static init(connection) {
    super.init({
      content_id: {
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
      tableName: 'content_has_log',
    })
  }
}

ContentHasLog.associations = (models) => {
  ContentHasLog.belongsTo(models.Content, { foreignKey: 'content_id' })
  ContentHasLog.belongsTo(models.Log, { foreignKey: 'log_id' })
}

export default ContentHasLog
