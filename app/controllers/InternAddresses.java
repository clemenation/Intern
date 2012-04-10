package controllers;

import play.*;
import play.mvc.*;

import models.InternAddress;

@Check("admin")
@With(Secure.class)
@CRUD.For(InternAddress.class)
public class InternAddresses extends CRUD {

}
