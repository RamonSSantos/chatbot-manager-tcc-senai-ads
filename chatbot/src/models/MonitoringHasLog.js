import { Model, DataTypes } from 'sequelize'

class MonitoringHasLog extends Model {
  static init(connection) {
    super.init({
      monitoring_id: {
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
      tableName: 'monitoring_has_log',
    })
  }
}

MonitoringHasLog.associations = (models) => {
  MonitoringHasLog.belongsTo(models.Monitoring, { foreignKey: 'monitoring_id' })
  MonitoringHasLog.belongsTo(models.Log, { foreignKey: 'log_id' })
}

export default MonitoringHasLog
