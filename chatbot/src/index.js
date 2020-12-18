import express, { json, urlencoded } from 'express'
import morgan from 'morgan'

import routes from './routes'

require('dotenv').config()
require('express-async-errors')
require('./database')

const app = express()
app.use(json())
app.use(urlencoded({ extended: true }))
app.use(morgan('dev'))
app.use(routes)

app.listen(process.env.PORT || 3333)
