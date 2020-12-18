import User from '@/models/User'
import UserService from '@/services/UserService'

async function getUser(agent) {
  const {
    fullname, registration, email, cpf, cellphone,
  } = agent.parameters

  let user
  let errorMessage
  try {
    const data = {
      fullname, registration, email, cpf, cellphone,
    }

    const response = await UserService.createUser(data)
    user = response.data

    let payload
    const fullName = user.fullname.split(' ')
    const firstName = fullName[0]

    if (user.status === 1) {
      // User already exists with status enabled
      agent.context.set({
        name: 'user-validation-followup',
        lifespan: 10,
        parameters: { firstName, userId: user.id, registration: user.registration },
      })
      agent.setFollowupEvent({ name: 'AuthenticatedUser' })
      agent.add('')
    } else if (user.status === 2) {
      // User already exists with status disabled
      payload = `${firstName}, o seu cadastro está desativado!\n\n`
      payload += `Entre em contato com o administrador.`

      agent.add(payload)
    } else if (user.status === 3) {
      // User created with status approval
      payload = `${firstName}, cadastro criado com sucesso!\n\n`
      payload += `No momento está em análise de aprovação.`

      agent.add(payload)
    }
  } catch (err) {
    // TODO
    errorMessage = err.response.data.message
    agent.add(errorMessage)
  }
}

async function userValidation(agent) {
  const { firstName, registration } = agent.parameters

  try {
    const response = await User.findOne({ where: { registration } })
    const user = response.dataValues

    if (user.registration === Number(registration)) {
      agent.context.set({
        name: 'user-validation-followup',
        lifespan: 10,
        parameters: { firstName, userId: user.id, registration: user.registration },
      })
      agent.setFollowupEvent({ name: 'AuthenticatedUser' })
      agent.add('')
    } else {
      agent.add(`${firstName}, os dados informados não correspondem. Tente novamente!`)
    }
  } catch (error) {
    if (error.name === 'SequelizeConnectionRefusedError') {
      agent.add(`Não foi possível realizar a consulta!\n\n`
        + `Error name: ${error.name}\n\n`
        + `${error.parent}`)
    } else {
      agent.add(`${firstName}, esse e-mail não está cadastrado. Tente novamente!`)
    }
  }
}

export {
  getUser,
  userValidation,
}
