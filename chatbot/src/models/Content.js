import { Model, DataTypes } from 'sequelize'

class Content extends Model {
  static init(connection) {
    super.init({
      id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
        autoIncrement: true,
      },
      title: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      status: {
        type: DataTypes.INTEGER,
        allowNull: false,
      },
      description: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      attachment: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      topic_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
          model: 'Topic',
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
      tableName: 'content',
    })
  }
}

export default Content
