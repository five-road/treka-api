package com.example.ieumapi.plan.exception;

import com.example.ieumapi.global.exception.CustomBaseException;

public class NoteException extends CustomBaseException {
    public NoteException(NoteError error) { super(error); }
}
