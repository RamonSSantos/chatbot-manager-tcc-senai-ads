import { Model, DataTypes } from 'sequelize'

class Monitoring extends Model {
  static init(connection) {
    super.init({
      id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
        autoIncrement: true,
      },
      status: {
        type: DataTypes.INTEGER,
        allowNull: false,
      },
      user_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
          model: 'User',
          key: 'id',
        },
      },
      content_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
          model: 'Content',
          key: 'id',
        },
      },
    }, {
      sequelize: connection,
      tableName: 'monitoring',
    })
  }
}

export default Monitoring
