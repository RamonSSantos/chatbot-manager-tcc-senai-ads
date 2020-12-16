<template>
  <v-card class="grey lighten-4 my-4">
    <Breadcrumb />
    <v-card-title class="text-uppercase grey--text text--darken-1 pt-0 pl-6">
      Novo Conteúdo
    </v-card-title>
    <v-stepper v-model="stepper" alt-labels>
      <v-stepper-header>
        <v-stepper-step :complete="stepper > 1" step="1" class="text-center">
          Informações Gerais
          <small>Identificação e descrição do conteúdo</small>
        </v-stepper-step>
        <v-divider></v-divider>
        <v-stepper-step :complete="stepper > 2" step="2">
          Informações de Entrada/Saída
          <small>Dados que serão enviados ao usuário</small>
        </v-stepper-step>
        <v-divider></v-divider>
        <v-stepper-step step="3">
          Informações Condicionais
          <small>Regras utilizadas para direcionar o conteúdo</small>
        </v-stepper-step>
      </v-stepper-header>
      <v-stepper-items>
        <v-stepper-content step="1" class="pa-0">
          <v-card outlined>
            <v-card-text class="py-0">
              <v-container>
                <v-form
                  ref="generalInformationForm"
                  v-model="generalInformation"
                  lazy-validation
                >
                  <v-row justify="center">
                    <v-col cols="8">
                      <v-autocomplete
                        ref="topicElement"
                        v-model="newContent.topic"
                        :items="topics"
                        item-text="description"
                        :rules="[rules.required]"
                        label="Tema*"
                        no-data-text="Selecione algum tema"
                        return-object
                        clearable
                        outlined
                        required
                      >
                      </v-autocomplete>
                    </v-col>
                  </v-row>
                  <v-row justify="center">
                    <v-col cols="8">
                      <v-text-field
                        v-model="newContent.title"
                        :rules="[rules.required, rules.titleLength]"
                        counter="45"
                        label="Título*"
                        outlined
                        required
                      >
                      </v-text-field>
                    </v-col>
                  </v-row>
                  <v-row justify="center">
                    <v-col cols="8">
                      <v-textarea
                        v-model="newContent.description"
                        :rules="[rules.descriptionLength]"
                        counter="255"
                        label="Descrição"
                        rows="4"
                        clearable
                        outlined
                        auto-grow
                      >
                      </v-textarea>
                    </v-col>
                  </v-row>
                </v-form>
                <small class="red--text">*Campos obrigatórios!</small>
              </v-container>
            </v-card-text>
            <v-card-actions class="grey lighten-4 py-3">
              <v-spacer></v-spacer>
              <v-btn @click="goToContentsPage(false)" text>Cancelar</v-btn>
              <v-btn
                @click="goToinputOutputInformationStep"
                color="primary"
                text
              >
                Avançar
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-stepper-content>
        <v-stepper-content step="2" class="pa-0">
          <v-card outlined>
            <v-card-text class="py-0">
              <v-container>
                <v-form
                  ref="inputOutputInformationForm"
                  v-model="inputOutputInformation"
                  lazy-validation
                >
                  <v-row justify="center">
                    <v-col cols="8">
                      <v-subheader>
                        É possível adicionar até 5 perguntas!
                      </v-subheader>
                      <v-text-field
                        v-model="question"
                        :rules="[rules.questionAndAnswerLength]"
                        counter="150"
                        @click:append-outer="addNewQuestion"
                        @keydown.enter="addNewQuestion"
                        label="Informe a pergunta"
                        prepend-inner-icon="mdi-help-rhombus"
                        append-outer-icon="mdi-plus-circle"
                        clearable
                        outlined
                      >
                      </v-text-field>
                      <v-card
                        v-if="questions.length > 0"
                        class="mx-auto"
                        max-width="600"
                      >
                        <v-list>
                          <v-subheader>Perguntas</v-subheader>
                          <v-list-item v-for="(text, i) in questions" :key="i">
                            <v-list-item-content>
                              <v-list-item-title v-text="text">
                              </v-list-item-title>
                            </v-list-item-content>
                            <v-list-item-action>
                              <v-btn @click="removeQuestion(i)" icon>
                                <v-icon>mdi-close-circle</v-icon>
                              </v-btn>
                            </v-list-item-action>
                          </v-list-item>
                        </v-list>
                      </v-card>
                    </v-col>
                  </v-row>
                  <v-row justify="center">
                    <v-col cols="8">
                      <v-subheader>
                        É possível adicionar até 3 respostas!
                      </v-subheader>
                      <v-text-field
                        v-model="answer"
                        :rules="[rules.questionAndAnswerLength]"
                        counter="150"
                        @click:append-outer="addNewAnswer"
                        @keydown.enter="addNewAnswer"
                        label="Informe a resposta*"
                        prepend-inner-icon="mdi-chat-alert"
                        append-outer-icon="mdi-plus-circle"
                        clearable
                        outlined
                      >
                      </v-text-field>
                      <v-card
                        v-if="answers.length > 0"
                        class="mx-auto"
                        max-width="600"
                      >
                        <v-list>
                          <v-subheader>Respostas</v-subheader>
                          <v-list-item v-for="(text, i) in answers" :key="i">
                            <v-list-item-content>
                              <v-list-item-title v-text="text">
                              </v-list-item-title>
                            </v-list-item-content>
                            <v-list-item-action>
                              <v-btn @click="removeAnswer(i)" icon>
                                <v-icon>mdi-close-circle</v-icon>
                              </v-btn>
                            </v-list-item-action>
                          </v-list-item>
                        </v-list>
                      </v-card>
                    </v-col>
                  </v-row>
                  <v-row justify="center">
                    <v-col cols="8">
                      <v-subheader>
                        É possível adicionar somente 1 arquivo!
                      </v-subheader>
                      <v-file-input
                        v-model="attachment"
                        accept="image/png, image/jpeg, image/bmp, .pdf"
                        :rules="[rules.attachmentSize, rules.attachmentType]"
                        @change="addAttachment"
                        label="Informe o arquivo"
                        prepend-icon=""
                        prepend-inner-icon="mdi-paperclip"
                        truncate-length="15"
                        show-size
                        chips
                        clearable
                        outlined
                      >
                      </v-file-input>
                    </v-col>
                  </v-row>
                </v-form>
                <small class="red--text">*Campos obrigatórios!</small>
              </v-container>
            </v-card-text>
            <v-card-actions class="grey lighten-4 py-3">
              <v-btn @click="stepper = 1" color="warning" text>
                Voltar
              </v-btn>
              <v-spacer></v-spacer>
              <v-btn @click="goToContentsPage(false)" text>Cancelar</v-btn>
              <v-btn
                @click="goToConditionsInformationStep"
                color="primary"
                text
              >
                Avançar
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-stepper-content>
        <v-stepper-content step="3" class="pa-0">
          <v-card outlined>
            <v-card-text class="py-0">
              <v-container>
                <v-form
                  ref="conditionsInformationForm"
                  v-model="conditionsInformation"
                  lazy-validation
                >
                  <v-row justify="center">
                    <v-col cols="8">
                      <v-subheader>
                        As palavras-chaves devem ter entre 3 a 20 caracteres
                      </v-subheader>
                      <v-combobox
                        v-model="keywords"
                        :rules="[rules.listKeywordsLength]"
                        counter="5"
                        label="Palavras-chaves"
                        hide-selected
                        multiple
                        small-chips
                        deletable-chips
                        clearable
                        outlined
                      >
                      </v-combobox>
                    </v-col>
                  </v-row>
                  <v-row justify="center">
                    <v-col cols="8">
                      <v-autocomplete
                        v-model="newContent.sector"
                        :items="sectors"
                        item-text="description"
                        label="Setor"
                        no-data-text="Selecione algum setor"
                        return-object
                        clearable
                        outlined
                      >
                      </v-autocomplete>
                    </v-col>
                  </v-row>
                </v-form>
                <small class="red--text"
                  >Preencha pelo menos um dos campos!</small
                >
              </v-container>
            </v-card-text>
            <v-card-actions class="grey lighten-4 py-3">
              <v-btn @click="stepper = 2" color="warning" text>
                Voltar
              </v-btn>
              <v-spacer></v-spacer>
              <v-btn @click="goToContentsPage(false)" text>Cancelar</v-btn>
              <v-btn @click="addNewContent" color="primary" dark>
                Concluir
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-stepper-content>
      </v-stepper-items>
    </v-stepper>
  </v-card>
</template>

<script>
import EventBus from '@/services/EventBus'
import Breadcrumb from '@/components/breadcrumb/Breadcrumb'
import SectorService from '@/services/SectorService'
import TopicService from '@/services/TopicService'
import ContentService from '@/services/ContentService'

export default {
  name: 'ContentNew',
  components: {
    Breadcrumb,
  },
  data: () => ({
    stepper: 1,
    generalInformation: true,
    inputOutputInformation: true,
    conditionsInformation: true,
    newContent: {
      title: null,
      description: null,
      attachment: null,
      topic: null,
      sector: null,
      question: [],
      answer: [],
      keyword: [],
    },
    topics: [],
    question: null,
    questions: [],
    answer: null,
    answers: [],
    attachment: null,
    keywords: null,
    sectors: [],
    rules: {
      required: (v) => !!v || 'Preencha este campo!',
      titleLength: (v) =>
        (v && v.length >= 5 && v.length <= 60) ||
        'O título deve ter entre 5 a 45 caracteres',
      descriptionLength: (v) =>
        v === null ||
        v === '' ||
        (v.length >= 10 && v.length <= 255) ||
        'A descrição deve ter entre 10 a 255 caracteres',
      questionAndAnswerLength: (v) =>
        (v && v.length >= 10 && v.length <= 150) ||
        v == null ||
        'A pergunta deve ter entre 10 a 150 caracteres',
      attachmentSize: (v) =>
        !v || v.size < 1000000 || 'O arquivo deve ter no máximo 1MB',
      attachmentType: (v) =>
        !v ||
        v.type === 'image/png' ||
        v.type === 'image/jpeg' ||
        v.type === 'image/bmp' ||
        v.type === 'application/pdf' ||
        'Formatos permitidos: PNG, JPEG, BMP e PDF',
      listKeywordsLength: (v) =>
        v == null ||
        v == '' ||
        v.length <= 5 ||
        'Informe até 5 palavras-chaves',
    },
  }),
  created() {
    this.getTopics()
    this.getSectors()
  },
  mounted() {
    this.$refs.topicElement.focus()
  },
  methods: {
    async getTopics() {
      const response = await TopicService.getAllActiveTopics()
      this.topics = response.data
    },
    async getSectors() {
      const response = await SectorService.getAllActiveSectors()
      this.sectors = response.data
    },
    goToinputOutputInformationStep() {
      if (this.$refs.generalInformationForm.validate()) {
        this.stepper = 2
      }
    },
    goToConditionsInformationStep() {
      if (
        this.questions.length <= 5 &&
        this.answers.length <= 3 &&
        this.$refs.inputOutputInformationForm.validate()
      ) {
        this.stepper = 3
      }
    },
    addNewQuestion() {
      if (
        this.questions.length < 5 &&
        this.question.length >= 10 &&
        this.question.length <= 150
      ) {
        this.questions.push(this.question)
        this.question = null
      }
    },
    removeQuestion(index) {
      this.questions.splice(index, 1)
    },
    addNewAnswer() {
      if (
        this.answers.length < 3 &&
        this.answer.length >= 10 &&
        this.answer.length <= 150
      ) {
        this.answers.push(this.answer)
        this.answer = null
      }
    },
    removeAnswer(index) {
      this.answers.splice(index, 1)
    },
    addAttachment(file) {
      if (file === undefined) {
        this.attachment = null
        this.newContent.attachment = null
      } else {
        this.attachment = file
        this.newContent.attachment = file.name
      }
    },
    async addNewContent() {
      if (this.$refs.conditionsInformationForm.validate()) {
        for (let q of this.questions) {
          this.newContent.question.push({ description: q })
        }

        for (let a of this.answers) {
          this.newContent.answer.push({ description: a })
        }

        if (this.keywords != null) {
          for (let k of this.keywords) {
            this.newContent.keyword.push({ description: k })
          }
        }

        if (this.attachment !== null) {
          const response = await ContentService.uploadAttachment(
            this.attachment
          )
          this.newContent.attachment = response.data.fileName
        }

        await ContentService.new(this.newContent)
        this.goToContentsPage(true)
      }
    },
    goToContentsPage(updateTable) {
      EventBus.$emit('contents', updateTable)
      this.$router.push({ name: 'Contents' })
    },
  },
}
</script>
