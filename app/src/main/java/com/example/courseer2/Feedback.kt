package com.example.courseer2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.util.Properties
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class Feedback : Fragment() {
    private lateinit var submitButton: Button
    private lateinit var textBox: EditText
    private lateinit var rootview: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview = inflater.inflate(R.layout.fragment_feedback, container, false)
        submitButton = rootview.findViewById(R.id.submitButton)
        textBox = rootview.findViewById(R.id.feedbackInput)
        val feedbackMail: String = textBox.getText().toString()

        submitButton.setOnClickListener{
            sendToEmail(feedbackMail)
        }
    // Inflate the layout for this fragment
        return rootview
    }
    private fun sendToEmail(feedback: String){
        // setup email, and the message
        val emailAddress: String = "noreplycourseer@gmail.com"
        val sender: String = "courseer.dummy@gmail.com"
        val pass: String = "capstonegroup2"
        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
        }
        // auth email and pass
        val session = Session.getInstance(props, object : javax.mail.Authenticator(){
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(sender, pass)
            }
        })
        try {
            val message = MimeMessage(session)
            message.setFrom(InternetAddress(sender))
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailAddress))
            message.setSubject("CourSeer: Feedback")
            message.setText(feedback)
            Transport.send(message)
            Toast.makeText(requireContext(), "Email sent successfully.", Toast.LENGTH_LONG).show()
        }catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                "Failed to send an email",
                Toast.LENGTH_LONG
            ).show()
        }

    }


}