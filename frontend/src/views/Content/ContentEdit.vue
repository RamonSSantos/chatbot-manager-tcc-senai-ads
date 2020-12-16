<template>
  <v-dialog
    v-model="dialog"
    max-width="600px"
    @click:outside="closeDialog(false)"
    @keydown.esc="closeDialog(false)"
    transition="slide-x-reverse-transition"
    scrollable
  >
    <v-card>
      <v-card-title>
        <span class="headline">Editar Conteúdo</span>
      </v-card-title>
      <v-card-text class="pb-0">
        <v-container>
          <v-form ref="editContent" v-model="valid" lazy-validation>
            <v-expansion-panels v-model="panel" focusable>
              <v-expansion-panel>
                <v-expansion-panel-header class="text-uppercase">
                  Informações Gerais
                </v-expansion-panel-header>
                <v-expansion-panel-content>
                  <v-row>
                    <v-col cols="12">
                      <v-text-field
                        ref="titleElement"
                        v-model="editObject.title"
                        :rules="[rules.required, rules.titleLength]"
                        label="Título*"
                        :disabled="disabled"
                        outlined
                        required
                      >
                      </v-text-field>
                    </v-col>
                    <v-col cols="12">
                      <v-autocomplete
                        v-model="editObject.topic"
                        :items="topics"
                        item-text="description"
                        label="Tema*"
                        no-data-text="Selecione algum tema"
                        return-object
                        clearable
                        outlined
                        required
                      >
                      </v-autocomplete>
                    </v-col>
                    <v-col cols="12">
                      <v-textarea
                        v-model="editObject.description"
                        :rules="[rules.descriptionLength]"
                        label="Descrição"
                        :disabled="disabled"
                        rows="4"
                        auto-grow
                        outlined
                      >
                      </v-textarea>
                    </v-col>
                  </v-row>
                </v-expansion-panel-content>
              </v-expansion-panel>
              <v-expansion-panel>
                <v-expansion-panel-header class="text-uppercase">
                  Informações de Entrada/Saída
                </v-expansion-panel-header>
                <v-expansion-panel-content>
                  <v-row>
                    <v-col cols="12">
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
                        v-if="
                          editObject.question && editObject.question.length > 0
                        "
                        class="mx-auto"
                        max-width="600"
                      >
                        <v-list>
                          <v-subheader>Perguntas</v-subheader>
                          <v-list-item
                            v-for="(text, i) in editObject.question"
                            :key="i"
                          >
                            <v-list-item-content>
                              <v-list-item-title v-text="text.description">
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
                    <v-col cols="12">
                      <v-subheader
                        >É possível adicionar até 3 respostas!</v-subheader
                      >
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
                        v-if="editObject.answer && editObject.answer.length > 0"
                        class="mx-auto"
                        max-width="600"
                      >
                        <v-list>
                          <v-subheader>Respostas</v-subheader>
                          <v-list-item
                            v-for="(text, i) in editObject.answer"
                            :key="i"
                          >
                            <v-list-item-content>
                              <v-list-item-title v-text="text.description">
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
                    <v-col cols="12">
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
                        <template v-slot:append-outer>
                          <v-icon
                            color="green darken-2"
                            @click.stop="downloadAttachment"
                            >mdi-download</v-icon
                          >
                        </template>
                      </v-file-input>
                    </v-col>
                  </v-row>
                </v-expansion-panel-content>
              </v-expansion-panel>
              <v-expansion-panel>
                <v-expansion-panel-header class="text-uppercase">
                  Informações Condicionais
                </v-expansion-panel-header>
                <v-expansion-panel-content>
                  <v-row>
                    <v-col cols="12">
                      <v-subheader>
                        As palavras-chaves devem ter entre 3 a 20 caracteres
                      </v-subheader>
                      <v-combobox
                        v-model="editObject.keyword"
                        :rules="[rules.listKeywordsLength]"
                        id="id"
                        item-text="description"
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
                    <v-col cols="12">
                      <v-autocomplete
                        v-model="sector"
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
                </v-expansion-panel-content>
              </v-expansion-panel>
            </v-expansion-panels>
          </v-form>
        </v-container>
        <small class="red--text">*Campos obrigatórios!</small>
      </v-card-text>
      <v-divider></v-divider>
      <v-card-actions class="pt-0">
        <v-btn @click.stop="closeDialog(false)" color="grey darken-1" text>
          Fechar
        </v-btn>
        <v-spacer></v-spacer>
        <v-btn @click.stop="resetForm" color="warning darken-1" text>
          Limpar
        </v-btn>
        <v-btn
          :disabled="!valid"
          @click.stop="editContent"
          color="blue darken-1"
          text
        >
          Editar
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import EventBus from '@/services/EventBus'
import TopicService from '@/services/TopicService'
import SectorService from '@/services/SectorService'
import ContentService from '@/services/ContentService'

export default {
  name: 'ContentEdit',
  props: {
    object: {
      type: Object,
      default: null,
    },
    disabled: {
      type: Boolean,
      default: null,
    },
  },
  computed: {
    getChangesOnEditContent() {
      return this.object.title === this.editObject.title &&
        this.object.description === this.editObject.description &&
        this.object.attachment === this.editObject.attachment &&
        this.object.topic === this.editObject.topic &&
        this.object.sector === this.editObject.sector &&
        this.object.question === this.editObject.question &&
        this.object.answer === this.editObject.answer &&
        this.object.keyword === this.editObject.keyword
        ? true
        : false
    },
  },
  data: () => ({
    dialog: false,
    panel: 0,
    valid: true,
    editObject: {
      id: null,
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
    answer: null,
    attachment: null,
    sector: null,
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
        (v && v.length <= 5) || 'Informe até 5 palavras-chaves',
    },
  }),
  created() {
    this.dialog = true
    this.editObject = Object.assign({}, this.object)

    this.sector = this.editObject.sector

    this.getTopics()
    this.getSectors()
    this.getAttachment()
  },
  mounted() {
    this.$refs.titleElement.focus()
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
    async getAttachment() {
      if (this.editObject.attachment !== null) {
        const response = await ContentService.downloadAttachment(
          this.editObject.attachment
        )

        const contentType = response.headers['content-type']
        const blob = new Blob([response.data], { type: contentType })

        this.attachment = new File([blob], this.editObject.attachment)
      }
    },
    async downloadAttachment() {
      if (this.editObject.attachment !== null) {
        const response = await ContentService.downloadAttachment(
          this.editObject.attachment
        )

        const url = window.URL.createObjectURL(new Blob([response.data]))

        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', this.object.attachment)

        document.body.appendChild(link)
        link.click()
        link.remove()
      }
    },
    addNewQuestion() {
      if (
        this.editObject.question.length < 5 &&
        this.question.length >= 10 &&
        this.question.length <= 150
      ) {
        this.editObject.question.push({ description: this.question })
        this.question = null
      }
    },
    removeQuestion(index) {
      this.editObject.question.splice(index, 1)
    },
    addNewAnswer() {
      if (
        this.editObject.answer.length < 3 &&
        this.answer.length >= 10 &&
        this.answer.length <= 150
      ) {
        this.editObject.answer.push({ description: this.answer })
        this.answer = null
      }
    },
    removeAnswer(index) {
      this.editObject.answer.splice(index, 1)
    },
    addAttachment(file) {
      if (file === undefined) {
        this.attachment = null
        this.editObject.attachment = null
      } else {
        this.attachment = file
        this.editObject.attachment = file.name
      }
    },
    async editContent() {
      if (this.getChangesOnEditContent) {
        const notification = {
          type: 'yellow darken-2',
          message: 'Altere algum campo para realizar a alteração!',
          icon: 'mdi-alert-circle',
          isLight: true,
        }
        this.$store.dispatch('notification/add', notification, { root: true })
      } else if (this.$refs.editContent.validate()) {
        this.editObject.sector = this.sector

        if (this.attachment !== null) {
          const response = await ContentService.uploadAttachment(
            this.attachment
          )
          this.editObject.attachment = response.data.fileName
        }

        await ContentService.edit(this.editObject)
        this.closeDialog(true)
      }
    },
    resetForm() {
      this.$refs.editContent.reset()
      this.editObject.question = []
      this.editObject.answer = []
    },
    closeDialog(updateTable) {
      EventBus.$emit('contents', updateTable)
      this.dialog = false
      this.$router.push({ name: 'Contents' })
    },
  },
  watch: {
    'editObject.keyword'(values) {
      values.filter((item, index) => {
        if (typeof item !== 'object') {
          this.editObject.keyword.splice(index, 1, { description: item })
        }
      })
    },
  },
}
</script>
