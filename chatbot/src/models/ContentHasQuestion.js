import { Model, DataTypes } from 'sequelize'

class ContentHasQuestion extends Model {
  static init(connection) {
    super.init({
      content_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
      },
      question_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
      },
    }, {
      sequelize: connection,
      tableName: 'content_has_question',
    })
  }
}

ContentHasQuestion.associations = (models) => {
  ContentHasQuestion.belongsTo(models.Content, { foreignKey: 'content_id' })
  ContentHasQuestion.belongsTo(models.Question, { foreignKey: 'question_id' })
}

export default ContentHasQuestion
