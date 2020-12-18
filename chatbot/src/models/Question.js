import { Model, DataTypes } from 'sequelize'

class Question extends Model {
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
    }, {
      sequelize: connection,
      tableName: 'question',
    })
  }
}

export default Question
