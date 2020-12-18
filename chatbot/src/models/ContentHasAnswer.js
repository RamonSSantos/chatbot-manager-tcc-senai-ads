import { Model, DataTypes } from 'sequelize'

class ContentHasAnswer extends Model {
  static init(connection) {
    super.init({
      content_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
      },
      answer_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
      },
    }, {
      sequelize: connection,
      tableName: 'content_has_answer',
    })
  }
}

ContentHasAnswer.associations = (models) => {
  ContentHasAnswer.belongsTo(models.Content, { foreignKey: 'content_id' })
  ContentHasAnswer.belongsTo(models.Answer, { foreignKey: 'answer_id' })
}

export default ContentHasAnswer
