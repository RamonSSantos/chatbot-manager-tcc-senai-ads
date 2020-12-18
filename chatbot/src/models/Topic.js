import { Model, DataTypes } from 'sequelize'

class Topic extends Model {
  static init(connection) {
    super.init({
      id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
        autoIncrement: true,
      },
      description: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      status: {
        type: DataTypes.INTEGER,
        allowNull: false,
      },
    }, {
      sequelize: connection,
      tableName: 'topic',
    })
  }
}

export default Topic
