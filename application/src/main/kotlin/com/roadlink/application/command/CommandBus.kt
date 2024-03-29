package com.roadlink.application.command

import kotlin.reflect.KClass

interface CommandBus {
    fun <C : Command, R : CommandResponse> publish(command: C): R
}

interface CommandResponse

interface Command

interface CommandHandler<C : Command, R : CommandResponse> {
    fun handle(command: C): R
}

/**
 * This will be the event bus through which the various commands handled by the application will be sent.
 * The commands are mapped to their corresponding handlers, which will contain the execution logic.
 * Make sure that you've registered the command with their own command handler.*/
class SimpleCommandBus : CommandBus {

    private val handlers =
        mutableMapOf<Class<out Command>, CommandHandler<Command, CommandResponse>>()

    override fun <C : Command, R : CommandResponse> publish(command: C): R {
        val handler = handlers[command::class.java] as? CommandHandler<C, R>
            ?: throw IllegalStateException("No handler registered for command of type ${command::class.java}")
        return handler.handle(command)
    }

    fun registerHandler(handler: CommandHandler<out Command, out CommandResponse>) {
        val commandClass = handler::class.supertypes
            .firstOrNull { it.classifier == CommandHandler::class }?.arguments?.first()?.type?.classifier as? KClass<out Command>
            ?: throw IllegalArgumentException("Handler class does not implement CommandHandler")
        handlers[commandClass.java] = handler as CommandHandler<Command, CommandResponse>
    }
}