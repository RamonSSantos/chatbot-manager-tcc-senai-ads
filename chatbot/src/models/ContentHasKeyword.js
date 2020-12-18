import { Model, DataTypes } from 'sequelize'

class ContentHasKeyword extends Model {
  static init(connection) {
    super.init({
      content_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
      },
      keyword_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
      },
    }, {
      sequelize: connection,
      tableName: 'content_has_keyword',
    })
  }
}

ContentHasKeyword.associations = (models) => {
  ContentHasKeyword.belongsTo(models.Content, { foreignKey: 'content_id' })
  ContentHasKeyword.belongsTo(models.Keyword, { foreignKey: 'keyword_id' })
}

export default ContentHasKeyword
