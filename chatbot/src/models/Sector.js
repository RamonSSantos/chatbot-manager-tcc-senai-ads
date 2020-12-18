import { Model, DataTypes } from 'sequelize'

class Sector extends Model {
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
      tableName: 'sector',
    })
  }
}

export default Sector
