import { Model, DataTypes } from 'sequelize'

class User extends Model {
  static init(connection) {
    super.init({
      id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
        autoIncrement: true,
      },
      fullname: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      cpf: {
        type: DataTypes.BIGINT,
        allowNull: false,
      },
      email: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      registration: {
        type: DataTypes.BIGINT,
        allowNull: false,
      },
      status: {
        type: DataTypes.INTEGER,
        allowNull: false,
      },
      cellphone: {
        type: DataTypes.BIGINT,
        allowNull: false,
      },
      password: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      profile_id: {
        type: DataTypes.INTEGER,
        allowNull: true,
        references: {
          model: 'Profile',
          key: 'id',
        },
      },
      sector_id: {
        type: DataTypes.INTEGER,
        allowNull: true,
        references: {
          model: 'Sector',
          key: 'id',
        },
      },
    }, {
      sequelize: connection,
      tableName: 'user',
    })
  }
}

export default User
